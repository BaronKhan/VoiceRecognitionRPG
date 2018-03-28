package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class VoiceProcess {
    protected Activity mMainActivity;
    protected GlobalState mState;
    protected ContextActionMap mContextActionMap;

    protected Context mActionContext; //stores the name of the context

    protected IDictionary mDict = null;
    MaxentTagger mTagger;

    //Semantic Similarity Engine
    protected ILexicalDatabase mDb = null;

    public VoiceProcess(
            Activity mainActivity, GlobalState state, ContextActionMap contextActionMap) {
        mMainActivity = mainActivity;
        mState = state;
        mContextActionMap = contextActionMap;

        mTagger = null;

        //Use all senses, not just most frequent sense (slower but more accurate)
        WS4JConfiguration.getInstance().setMFS(false);

        try {
            // Load model
            String modelPath = Environment.getExternalStorageDirectory()
                    + "/english-left3words-distsim.tagger";
            File modelFile = new File(modelPath);
            if (modelFile.exists()) {
                mTagger = new MaxentTagger(modelPath);
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Error loading model: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    public Object processInput(String input) {
        String actionOutput = "";
        if (mDict == null) { return "Error: WordNet not loaded."; }

        mState.actionFailed();  // by default, we fail to execute input

        //tokenise and tag
        List<String> words = new ArrayList<>();
        words.addAll(new LinkedList<>(Arrays.asList(input.split(" "))));
        removeContractions(words);
        List<String> tags = getTags(input);
        if (words.size()!=tags.size()) {
            throw new AssertionError("Error: no. of words("+words.size()
                    +") != no.of tags("+tags.size()+"), input = "+input);
        }

        String chosenAction = getBestAction(words, tags);

        if (mContextActionMap.isValidAction(chosenAction)) {
            Context currentTarget = getBestTarget(words, tags);
            String chosenContext = getBestContext(words, tags, chosenAction.equals("use"));
            if (mContextActionMap.isValidContext(chosenContext)) {
                if (mContextActionMap.get(chosenContext).get(chosenAction) == null) {
                    actionOutput += "You cannot " + chosenAction + " with that. Ignoring...\n";
                    chosenContext = "default";
                }
            } else { chosenContext = "default"; }
            if (mContextActionMap.get(chosenContext).get(chosenAction) == null) {
                actionOutput += "Intent not understood.";
            } else {
                actionOutput += mContextActionMap.get(chosenContext)
                        .get(chosenAction)
                        .run(mState, currentTarget);

            }
        } else { actionOutput = "Intent not understood."; }

        return actionOutput;
    }

    public void addDictionary(URL url) throws IOException {
        mDict = new Dictionary(url);
        mDict.open();
        mDb = new CustomWordNet(mDict);
    }

    public Context getActionContext() { return mActionContext; }

    private String getBestAction(List<String> words, List<String> tags) {
        List<Integer> candidateActions = getCandidateActions(tags);
        double bestScore = 0.8;
        int bestIndex = -1;
        String bestAction = "<none>";
        List<String> actionsList = mContextActionMap.getActions();
        for (int i: candidateActions) {
            String word = words.get(i);
            if (mContextActionMap.hasSynonym(word)) {
                words.remove(i);
                tags.remove(i);
                return mContextActionMap.getSynonymAction(word);
            }
            for (String action : actionsList) {
                if (word.equals(action)) {
                    words.remove(i);
                    tags.remove(i);
                    return action;
                }
                else {
                    if (!action.equals("use")) {
                        double score = calculateScore(action, word);
                        if (score > bestScore) {
                            bestScore = score;
                            bestIndex = i;
                            bestAction = action;
                        }
                    }
                }
            }
        }

        //Remove chosen action from list inputs
        if (bestIndex > -1) {
            words.remove(bestIndex);
            tags.remove(bestIndex);
        }

        return bestAction;
    }

    private Context getBestTarget(List<String> words, List<String> tags) {
        List<Integer> candidateTargets = getCandidateTargets(words, tags);
        List<Context> possibleTargetList = mContextActionMap.getPossibleTargets();
        if (candidateTargets == null || possibleTargetList == null ||
                candidateTargets.size() < 1 || possibleTargetList.size() < 1) {
            return mContextActionMap.mDefaultTarget;
        }
        double bestScore = 0.8;
        int bestIndex = -1;
        Context bestTarget = null;
        for (int i: candidateTargets) {
            String word = words.get(i);
            for (Context target : possibleTargetList) {
                String targetName = target.getName();
                if (word.equals(targetName)) {
                    words.remove(i);
                    tags.remove(i);
                    return target;
                } else {
                    if (target.descriptionHas(word)) {
                        words.remove(i);
                        tags.remove(i);
                        return target;
                    }
                    double score = calculateScore(word, targetName);
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                        bestTarget = target;
                    }
                }
            }
        }
        if (bestTarget == null) {
            return mContextActionMap.mDefaultTarget;
        } else {
            words.remove(bestIndex);
            tags.remove(bestIndex);
            return bestTarget;
        }
    }

    private String getBestContext(List<String> words, List<String> tags, boolean withUseAction) {
        List<String> candidateContext = getCandidateContext(words, tags, withUseAction);
        List<Context> possibleContextList = mContextActionMap.getPossibleContexts();
        if (candidateContext == null || possibleContextList == null ||
                candidateContext.size() < 1 || possibleContextList.size() < 1) {
            return "default";
        }
        double bestScore = 0.8;
        Context bestContext = null;
        String bestContextWord = "<none>";
        //Find best word
        for (String word : candidateContext) {
            for (Context context : possibleContextList) {
                String contextName = context.getName();
                if (word.equals(contextName)) {
                    bestScore = 1.0;
                    bestContext = context;
                    break;
                } else if (!word.equals(mContextActionMap.mDefaultTarget.getName())) {
                    if (context.descriptionHas(word)) {
                        bestScore = 1.0;
                        bestContext = context;
                        break;
                    }
                    double score = calculateScore(word, contextName);
                    if (score > bestScore) {
                        bestScore = score;
                        bestContext = context;
                    }
                }
            }
            //If "use", context could be action ("attack"). Find context that is not null for this
            //e.g. "use an attack", "use something to heal with"
            if (withUseAction) {
                List<String> actionList = mContextActionMap.getActions();
                String chosenAction = null;
                if ((mContextActionMap.hasSynonym(word))) {
                    chosenAction = mContextActionMap.getSynonymAction(word);
                } else if (actionList.contains(word)) {
                    chosenAction = word;
                }
                if (chosenAction != null) {
                    bestScore = 1.0;
                    bestContext = null;
                    bestContextWord = "<none>";
                    //Set bestContext to a random context that works with action
                    //https://stackoverflow.com/a/1066607/8919086
                    for (String key : mContextActionMap.mMap.keySet()) {
                        if (mContextActionMap.mMap.get(key).get(chosenAction) != null) {
                            for (Context context : possibleContextList) {
                                if (context.getContext().equals(key)) {
                                    bestContext = context;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (bestContext != null) {
            bestContextWord = bestContext.getContext();
            mActionContext = bestContext;
        }

        return bestContextWord;
    }

    private List<Integer> getCandidateActions(List<String> tags) {
        List<Integer> candidateActions = new ArrayList<>();
        for (int i=0; i<tags.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (tag.charAt(0) == 'v' || tag.charAt(0) == 'n') {
                candidateActions.add(i);
            }
        }
        return candidateActions;
    }

    private List<Integer> getCandidateTargets(List<String> words, List<String> tags) {
        List<Integer> candidateTargets = new ArrayList<>();
        boolean foundWithUsing = false;
        for (int i=0; i<tags.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (words.get(i).equals("with") || words.get(i).equals("using")) {
                foundWithUsing = true;
            }
            if ((!foundWithUsing) && (tag.charAt(0) == 'n')) {
                candidateTargets.add(i);
            }
        }
        return candidateTargets;
    }

    private List<String> getCandidateContext(List<String> words, List<String> tags) {
        return getCandidateContext(words, tags, false);
    }

    private List<String> getCandidateContext(List<String> words, List<String> tags,
                                            boolean withUseAction) {
        List<String> candidateItems = new ArrayList<>();
        boolean foundWithUsing = withUseAction;
        for (int i=0; i<words.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (foundWithUsing &&
                    (tag.charAt(0) == 'v' || tag.charAt(0) == 'n' || tag.charAt(0) == 'j')) {
                candidateItems.add(words.get(i));
            } else if (words.get(i).equals("with") || words.get(i).equals("using")) {
                foundWithUsing = true;
            }
        }
        return candidateItems;
    }

    private double calculateScore(String word1, String word2) {
        if (mDb == null) {
            Toast.makeText(mMainActivity, "Error while parsing: ILexicalDatabase not loaded",
                    Toast.LENGTH_LONG).show();
            return 0.0;
        }
        double score1, score2, score;
        try {
            score1 = new WuPalmer(mDb).calcRelatednessOfWords(word1, word2);
            score2 = new Lin(mDb).calcRelatednessOfWords(word1, word2);
            if (score2 > 0) {
                score = (score1*0.75) + (score2*0.25);
            } else {
                score = score1;
            }
        } catch (Exception e) {
            Toast.makeText(mMainActivity, "Error while parsing: Unsupported POS Pairs",
                    Toast.LENGTH_LONG).show();
            score = 0.0;
        }
        return score;
    }

    private List<String> getTags(String input) {
        assert(mTagger != null);
        String taggedWords = mTagger.tagString(input);
        List<String> tags = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=_)[A-Z$]+(?=\\s|$)").matcher(taggedWords);
        while (m.find()) {
            tags.add(m.group());
        }
        return tags;
    }

    private void removeContractions(List<String> words) {
        for (int i = 0; i < words.size(); ++i) {
            if (words.get(i).contains("'")) {
                words.remove(i);
            }
        }
    }
}
