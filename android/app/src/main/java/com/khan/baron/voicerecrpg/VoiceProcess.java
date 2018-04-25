package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.util.Pair;
import android.widget.Toast;

import com.khan.baron.voicerecrpg.actions.Action;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class VoiceProcess {
    private Activity mMainActivity;
    private GlobalState mState;
    private ContextActionMap mContextActionMap;

    private Entity mActionContext; //Stores the name of the context

    private IDictionary mDict = null;

    //Made static to avoid out-of-space GC allocation errors
    protected static MaxentTagger sTagger = null;

    //Confirmation state
    private boolean mIsAmbiguousAction = false;
    private boolean mIsAmbiguousTarget = false;
    private boolean mIsAmbiguousContext = false;
    private Pair<String, String> mAmbiguousActionMap = null;    //mapping: synonym first
    private Pair<String, String> mAmbiguousTargetMap = null;
    private Pair<String, String> mAmbiguousContextMap = null;
    private Map<Pair<String, String>, Integer> mAmbiguousCount = new HashMap<>();

    private boolean mExpectingReply = false;
    private Action mPendingAction = null;
    private Entity mPendingCurrentTarget = null;

    public VoiceProcess(
            Activity mainActivity, GlobalState state, ContextActionMap contextActionMap) {
        mMainActivity = mainActivity;
        mState = state;
        mContextActionMap = contextActionMap;

        if (sTagger == null) {
            try {
                // Load model
                String modelPath = Environment.getExternalStorageDirectory()
                        + "/english-left3words-distsim.tagger";
                File modelFile = new File(modelPath);
                if (modelFile.exists()) {
                    sTagger = new MaxentTagger(modelPath);
                }
            } catch (Exception e) {
                Toast.makeText(mainActivity, "Error loading model: " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public Object processInput(String input) {
        String actionOutput = "";
        if (mDict == null) { return "Error: WordNet not loaded."; }

        if (mExpectingReply) {
            mExpectingReply = false;
            if (input.contains("yes") || input.contains("yeah") || input.contains("yup")) {
                if (mPendingAction != null && mPendingCurrentTarget != null) {
                    return addAmbiguousSynonyms() + "\n"
                            + mPendingAction.execute(mState, mPendingCurrentTarget);
                }
            } else {
                return "Intent ignored.";
            }
        } else {
            mPendingAction = null;
            mPendingCurrentTarget = null;
        }

        mState.actionFailed();  // by default, we fail to execute input
        mIsAmbiguousAction = false;
        mIsAmbiguousTarget = false;
        mIsAmbiguousContext = false;
        mAmbiguousActionMap = null;
        mAmbiguousTargetMap = null;
        mAmbiguousContextMap = null;

        // Tokenize and tag input
        List<String> words =
                new ArrayList<>(new LinkedList<>(Arrays.asList(input.toLowerCase().split(" "))));
        List<String> tags = getTags(input);
        removeContractions(words, tags);
        if (words.size()!=tags.size()) {
            throw new AssertionError("Error: no. of words("+words.size()
                    +") != no.of tags("+tags.size()+"), input = "+input);
        }

        // Check for learning phrase ("___ means ___")
        if (words.size() == 3 && words.contains("means")) {
            String firstWord = getFirstAction(words, tags);
            removeWordAtIndex(words, tags, words.indexOf("means"));
            String secondWord = getFirstAction(words, tags);
            if (firstWord != null && secondWord != null) {
                if (mContextActionMap.getActions().contains(secondWord)) {
                    return addSynonym(firstWord, secondWord);
                } else if (mContextActionMap.getActions().contains(firstWord)) {
                    return addSynonym(secondWord, firstWord);
                } else {
                    return "Sorry. Neither \"" + firstWord + "\" nor \"" + secondWord
                            + "\" are valid actions.";
                }
            } else { return "Intent not understood"; }
        }

        Pair<Integer, String> actionPair = getBestAction(words, tags, false);
        String chosenAction = actionPair.second;

        if (mContextActionMap.isValidAction(chosenAction)) {
            Entity currentTarget;
            String chosenContext;

            boolean usingAltSFS = useAltSlotFillingStructure(words, tags, actionPair.first);

            if (usingAltSFS) {
                // SFS: with/use CONTEXT ACTION TARGET
                chosenContext = getBestContext(words, tags, true);
                actionPair = getBestAction(words, tags);
                currentTarget = getBestTarget(words, tags, true);
            } else {
                // SFS: ACTION TARGET with/using CONTEXT
                actionPair = getBestAction(words, tags);
                currentTarget = getBestTarget(words, tags, false);
                chosenContext = getBestContext(words, tags, false);
            }
            chosenAction = actionPair.second;

            if (mContextActionMap.isValidContext(chosenContext)) {
                if (mContextActionMap.get(chosenContext).get(chosenAction) == null) {
                    actionOutput += "You cannot " + chosenAction + " with that. Ignoring...\n";
                    chosenContext = "default";
                }
            } else {
                chosenContext = "default";
            }

            if (mContextActionMap.get(chosenContext).get(chosenAction) == null) {
                actionOutput += "Intent not understood.";
            } else {
                Action action = mContextActionMap.get(chosenContext).get(chosenAction);
                //Check for ambiguous intent
                if (mIsAmbiguousAction || mIsAmbiguousTarget || mIsAmbiguousContext) {
                    mExpectingReply = true;
                    mPendingAction = action;
                    mPendingCurrentTarget = currentTarget;
                    String pendingIntent =
                            buildIntent(chosenAction, currentTarget,
                                    mActionContext, usingAltSFS);
                    actionOutput += "Intent not understood.\nDid you mean, \""+pendingIntent+"\"? "
                            +"(yes/no)";
                } else {
                    actionOutput += action.execute(mState, currentTarget);
                }
            }
        } else {
            if (words.contains("use") || words.contains("with") || words.contains("using")) {
                getBestContext(words, tags, true);  // Sets mActionContext
                if (mActionContext != null) {
                    actionOutput = "What do you want to use the "+mActionContext.getName()+" for?";
                } else {
                    actionOutput = "Intent not understood.";
                }
            } else {
                actionOutput = "Intent not understood.";
            }
        }

        return actionOutput;
    }

    private String buildIntent(String action, Entity target, Entity context, boolean usingAltSFS) {
        Entity defaultTarget = mContextActionMap.getDefaultTarget();
        if (usingAltSFS) {
            return (context == null || context.getName().equals("default")
                        ? ""
                        : "use "+context.getName()+" to ")
                    +action
                    +((target != defaultTarget) ? " the "+target.getName() : "");
        } else {
            return action
                    +((target != defaultTarget) ? " the "+target.getName() : "")
                    +(context == null || context.getName().equals("default")
                        ? ""
                        : " with "+context.getName());
        }
    }

    private String addSynonym(String synonym, String word) {
        mContextActionMap.addSynonym(synonym, word);
        return "Added synonym: " + synonym + " --> " + word + "\n";
    }

    private String addAmbiguousSynonyms() {
        String output = "";
        if (mAmbiguousActionMap != null) {
            if (mAmbiguousCount.containsKey(mAmbiguousActionMap)) {
                output += addSynonym(mAmbiguousActionMap.first, mAmbiguousActionMap.second);
            } else {
                mAmbiguousCount.put(mAmbiguousActionMap, 1);
            }
        }
        if (mAmbiguousTargetMap != null) {
            if (mAmbiguousCount.containsKey(mAmbiguousTargetMap)) {
                output += addSynonym(mAmbiguousTargetMap.first, mAmbiguousTargetMap.second);
            } else {
                mAmbiguousCount.put(mAmbiguousTargetMap, 1);
            }
        }
        if (mAmbiguousContextMap != null) {
            if (mAmbiguousCount.containsKey(mAmbiguousContextMap)) {
                output += addSynonym(mAmbiguousContextMap.first, mAmbiguousContextMap.second);
            } else {
                mAmbiguousCount.put(mAmbiguousContextMap, 1);
            }
        }
        return output;
    }

    public void addDictionary(URL url) throws IOException {
        mDict = new Dictionary(url);
        mDict.open();
        //Semantic Similarity Engine
        SemanticSimilarity.getInstance().init(new CustomWordNet(mDict));
    }

    public IDictionary getDictionary() { return mDict; }

    public Entity getActionContext() { return mActionContext; }

    private Pair<Integer, String> getBestAction(List<String> words, List<String> tags) {
        return getBestAction(words, tags, true);
    }

    private Pair<Integer, String> getBestAction(
            List<String> words, List<String> tags, boolean deleteWord)
    {
        List<Integer> candidateActions = getCandidateActions(tags);
        double bestScore = 0.5; //0.7
        int bestIndex = -1;
        String bestAction = "<none>";
        List<String> actionsList = mContextActionMap.getActions();
        for (int i: candidateActions) {
            String word = words.get(i);
            //ignore with/use words
            if (!(word.equals("use") || word.equals("with") || word.equals("using")
                    || mContextActionMap.hasPossibleTarget(word)
                    || mContextActionMap.hasPossibleContext(word))) {
                if (mContextActionMap.hasSynonym(word)) {
                    if (deleteWord) {
                        removeWordAtIndex(words, tags, i);
                    }
                    return new Pair<>(i, mContextActionMap.getSynonymAction(word));
                }
                for (String action : actionsList) {
                    if (mContextActionMap.wordIsIgnored(word, action)) {
                        continue;
                    }
                    if (word.equals(action)) {
                        if (deleteWord) {
                            removeWordAtIndex(words, tags, i);
                        }
                        return new Pair<>(i, action);
                    }
                }
                for (String action : actionsList) {
                    double score = SemanticSimilarity.getInstance().calculateScore(action, word);
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                        bestAction = action;
                    }
                }
            }
        }

        if (bestIndex > -1) {
            if (bestScore > 0.5 && bestScore < 0.8) {
                mIsAmbiguousAction = true;
                mAmbiguousActionMap = new Pair<>(words.get(bestIndex), bestAction);
            }

            //Remove chosen action from list inputs
            if (deleteWord) { removeWordAtIndex(words, tags, bestIndex); }
        }

        return new Pair<>(bestIndex, bestAction);
    }

    private boolean useAltSlotFillingStructure(
            List<String> words, List<String> tags, int bestActionIndex)
    {
        List<String> keywords = Arrays.asList("use", "using", "with");
        for (String keyword : keywords) {
            if (words.contains(keyword)) {
                int keywordIndex = words.indexOf(keyword);
                if (keywordIndex < bestActionIndex) {
                    removeWordAtIndex(words, tags, keywordIndex);
                    return true;
                }
            }
        }
        return false;
    }

    private Entity getBestTarget(List<String> words, List<String> tags, boolean usingAltSFS) {
        List<Integer> candidateTargets = getCandidateTargets(words, tags, usingAltSFS);
        List<Entity> possibleTargetList = mContextActionMap.getPossibleTargets();
        if (candidateTargets == null || possibleTargetList == null ||
                candidateTargets.size() < 1 || possibleTargetList.size() < 1) {
            return mContextActionMap.mDefaultTarget;
        }
        double bestScore = 0.7; //0.8
        int bestIndex = -1;
        Entity bestTarget = null;
        for (int i: candidateTargets) {
            String word = words.get(i);
            for (Entity target : possibleTargetList) {
                String targetName = target.getName();
                if (mContextActionMap.wordIsIgnored(word, targetName)) { continue; }
                if (word.equals(targetName)) {
                    removeWordAtIndex(words, tags, i);
                    return target;
                } else {
                    if (target.descriptionHas(word)) {
                        removeWordAtIndex(words, tags, i);
                        return target;
                    }
                    double score = SemanticSimilarity.getInstance().calculateScore(word, targetName);
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
            if (bestScore > 0.7 && bestScore < 0.8) {
                mIsAmbiguousTarget = true;
                mAmbiguousTargetMap = new Pair<>(words.get(bestIndex), bestTarget.getName());
            }
            removeWordAtIndex(words, tags, bestIndex);

            return bestTarget;
        }
    }

    private String getBestContext(List<String> words, List<String> tags, boolean usingAltSFS) {
        List<Integer> candidateContext = getCandidateContext(words, tags, usingAltSFS);
        List<Entity> possibleContextList = mContextActionMap.getPossibleContexts();
        if (candidateContext == null || possibleContextList == null ||
                candidateContext.size() < 1 || possibleContextList.size() < 1) {
            return "default";
        }
        double bestScore = 0.6; //0.8
        Entity bestContext = null;
        int bestIndex = -1;
        String bestContextType = "<none>";
        //Find best word
        for (int i : candidateContext) {
            String word = words.get(i);
            for (Entity context : possibleContextList) {
                String contextName = context.getName();
                if (mContextActionMap.wordIsIgnored(word, contextName)
                        || mContextActionMap.getActions().contains(word)) { continue; }
                if (word.equals(contextName)) {
                    bestScore = 1.0;
                    bestContext = context;
                    bestIndex = i;
                    break;
                } else if (!word.equals(mContextActionMap.mDefaultTarget.getName())) {
                    if (context.descriptionHas(word)) {
                        bestScore = 1.0;
                        bestContext = context;
                        bestIndex = i;
                        break;
                    }
                    double score = SemanticSimilarity.getInstance().calculateScore(word, contextName);
                    if (score > bestScore) {
                        bestScore = score;
                        bestContext = context;
                        bestIndex = i;
                    }
                }
            }

            if (bestScore >= 1.0) { break; }
        }

        if (bestContext != null) {
            bestContextType = bestContext.getContext();
            mActionContext = bestContext;
            if (bestScore > 0.6 && bestScore < 0.8) {
                mIsAmbiguousContext = true;
                mAmbiguousContextMap = new Pair<>(words.get(bestIndex), bestContext.getName());
            }
            removeWordAtIndex(words, tags, bestIndex);
        }

        return bestContextType;
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

    private List<Integer> getCandidateTargets(
            List<String> words, List<String> tags,boolean usingAltSFS)
    {
        List<Integer> candidateTargets = new ArrayList<>();
        for (int i=0; i<tags.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (!usingAltSFS && (words.get(i).equals("with") || words.get(i).equals("using"))) {
                return candidateTargets;
            } else if ((tag.charAt(0) == 'n' || tag.charAt(0) == 'v'
                    || tag.charAt(0) == 'j' || tag.charAt(0) == 'i')) {
                candidateTargets.add(i);
            }
        }
        return candidateTargets;
    }

    private List<Integer> getCandidateContext(
            List<String> words, List<String> tags, boolean usingAltSFS)
    {
        List<Integer> candidateContext = new ArrayList<>();
        boolean foundWithUsing = usingAltSFS;
        for (int i=0; i<words.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (foundWithUsing &&
                    (tag.charAt(0) == 'v' || tag.charAt(0) == 'n' || tag.charAt(0) == 'j')) {
                candidateContext.add(i);
            } else if (words.get(i).equals("with") || words.get(i).equals("using")) {
                foundWithUsing = true;
            }
        }
        return candidateContext;
    }

    private List<String> getTags(String input) {
        assert(sTagger != null);
        String taggedWords = sTagger.tagString(input);
        List<String> tags = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=_)[A-Z$]+(?=\\s|$)").matcher(taggedWords);
        while (m.find()) {
            tags.add(m.group());
        }
        return tags;
    }

    private void removeContractions(List<String> words, List<String> tags) {
        for (int i = 0; i < words.size(); ++i) {
            if (words.get(i).contains("'")) {
                words.remove(i);
                tags.remove(i);
                tags.remove(i+1);
            }
        }
    }

    private void removeWordAtIndex(List<String> words, List<String> tags, int i) {
        words.remove(i);
        tags.remove(i);
    }

    private String getFirstAction(List<String> words, List<String> tags) {
        List<Integer> candidateActions = getCandidateActions(tags);
        if (candidateActions == null || candidateActions.size() == 0) { return null; }
        String action = words.get(candidateActions.get(0));
        removeWordAtIndex(words, tags, 0);
        return action;
    }

}
