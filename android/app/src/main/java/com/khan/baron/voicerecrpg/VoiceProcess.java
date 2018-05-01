package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
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
import edu.stanford.nlp.util.Triple;

public class VoiceProcess {
    private Activity mMainActivity;
    private GlobalState mState;
    private ContextActionMap mContextActionMap;

    private Entity mActionContext; //Stores the name of the context

    private IDictionary mDict = null;

    //Made static to avoid out-of-space GC allocation errors
    protected static MaxentTagger sTagger = null;

    private boolean mUsingAltSFS;

    private static boolean sGiveMultipleSuggestions = true;

    //Confirmation checking state
    private boolean mIsAmbiguous = false;
    private List<Triple<String, String, Double>> mAmbiguousActionCandidates = null;   //mapping: synonym first
    private List<Triple<String, Entity, Double>> mAmbiguousTargetCandidates = null;
    private List<Triple<String, Entity, Double>> mAmbiguousContextCandidates = null;
    private Map<Pair<String, String>, Integer> mAmbiguousCount = new HashMap<>();

    //Confirmation execution state
    private boolean mExpectingReply = false;
    private Pair<String, String> mAmbiguousPair = null;
    private String mPendingAction = null;
    private Entity mPendingTarget = null;
    private String mPendingContext = null;

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

    public static boolean isGivingMultipleSuggestions() {
        return sGiveMultipleSuggestions;
    }

    public static void setGiveMultipleSuggestions(boolean giveMultipleSuggestions) {
        sGiveMultipleSuggestions = giveMultipleSuggestions;
    }

    public Object processInput(String input) {
        String actionOutput = "";
        if (mDict == null) { return "Error: WordNet not loaded."; }

        if (mExpectingReply) {
            if (input.contains("yes") || input.contains("yeah") || input.contains("yup")) {
                if (mPendingAction != null && mPendingTarget != null) {
                    if (mContextActionMap.get(mPendingContext).get(mPendingAction) != null) {
                        mExpectingReply = false;
                        Action action =
                                mContextActionMap.get(mPendingContext).get(mPendingAction);
                        return addAmbiguousSynonyms(mAmbiguousPair) + "\n"
                                + action.execute(mState, mPendingTarget);
                    }
                }
                return "Intent not understood.";
            } else if (sGiveMultipleSuggestions && (input.contains("no") || input.contains("na")
                    || input.contains("nope") || input.contains("negative"))){
                // Try another suggestion until all suggestions are done
                return generateSuggestion();
            } else {
                mExpectingReply = false;
                return "Intent ignored.";
            }
        }

        mState.actionFailed();  // By default, we fail to execute input
        mActionContext = null;

        mIsAmbiguous = false;
        mPendingAction = null;
        mPendingTarget = null;

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

            mUsingAltSFS = useAltSlotFillingStructure(words, tags, actionPair.first);

            if (mUsingAltSFS) {
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
                if (mIsAmbiguous) {
                    mExpectingReply = true;
                    mPendingAction = chosenAction;
                    mPendingTarget = currentTarget;
                    mPendingContext = chosenContext;
                    Log.d("VoiceProcess", "action candidates = "+mAmbiguousActionCandidates+
                            "\ntarget candidates = "+mAmbiguousTargetCandidates+
                            "\ncontext candidates = "+mAmbiguousContextCandidates);
                    actionOutput += "Intent not understood.\n" + generateSuggestion();
                } else {
                    actionOutput += action.execute(mState, currentTarget);
                }
            }
        } else {
            if (words.contains("use") || words.contains("with") || words.contains("using") ||
                    words.contains("utilise"))
            {
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

    private String generateSuggestion() {
        if (mAmbiguousActionCandidates.size() > 0 || mAmbiguousTargetCandidates.size() > 0
                || mAmbiguousContextCandidates.size() > 0)
        {
            if (mAmbiguousActionCandidates.size() > 0) {
                getAmbiguousAction(mAmbiguousActionCandidates.get(0));
                mAmbiguousActionCandidates.remove(0);
            } else if (mAmbiguousTargetCandidates.size() > 0) {
                getAmbiguousTarget(mAmbiguousTargetCandidates.get(0));
                mAmbiguousTargetCandidates.remove(0);
            } else {
                getAmbiguousContext(mAmbiguousContextCandidates.get(0));
                mAmbiguousContextCandidates.remove(0);
            }
            String pendingIntent =
                    buildIntent(mPendingAction, mPendingTarget, mActionContext, mUsingAltSFS);
            return "Did you mean, \"" + pendingIntent + "\"? (yes/no)";
        } else {
            mExpectingReply = false;
            return "No more suggestions. Intent ignored.";
        }
    }

    private void getAmbiguousContext(Triple<String, Entity, Double> candidate) {
        String synonym = candidate.first;
        Entity context = candidate.second;
        mAmbiguousPair = new Pair<>(synonym, context.getName());
        mActionContext = context;
        if (mContextActionMap.isValidContext(context.getContext())) {
            if (mContextActionMap.get(context.getContext()).get(mPendingAction) == null) {
                mPendingContext = "default";
            } else { mPendingContext = "default"; }
        } else {
            mPendingContext = "default";
        }
    }

    private void getAmbiguousTarget(Triple<String, Entity, Double> candidate) {
        String synonym = candidate.first;
        Entity target = candidate.second;
        mAmbiguousPair = new Pair<>(synonym, target.getName());
        mPendingTarget = target;
    }

    private void getAmbiguousAction(Triple<String, String, Double> candidate) {
        String synonym = candidate.first;
        String action = candidate.second;
        mAmbiguousPair = new Pair<>(synonym, action);
        mPendingAction = mAmbiguousPair.second;
    }

    private String addSynonym(String synonym, String word) {
        mContextActionMap.addSynonym(synonym, word);
        return "Added synonym: " + synonym + " --> " + word + "\n";
    }

    private String addAmbiguousSynonyms(Pair<String, String> ambiguousPair) {
        String output = "";
        if (ambiguousPair != null) {
            if (mAmbiguousCount.containsKey(ambiguousPair)) {
                output += addSynonym(ambiguousPair.first, ambiguousPair.second);
            } else {
                mAmbiguousCount.put(ambiguousPair, 1);
            }
        }
        return output;
    }

    public void addDictionary(URL url) throws IOException {
        mDict = new Dictionary(url);
        mDict.open();
        //Semantic Similarity Engine
        SemanticSimilarity.getInstance().init(new CustomLexicalDatabase(mDict));
    }

    public IDictionary getDictionary() { return mDict; }

    public Entity getActionContext() { return mActionContext; }

    private Pair<Integer, String> getBestAction(List<String> words, List<String> tags) {
        return getBestAction(words, tags, true);
    }

    private Pair<Integer, String> getBestAction(
            List<String> words, List<String> tags, boolean deleteWord)
    {
        mAmbiguousActionCandidates = new ArrayList<>();
        List<Integer> candidateActions = getCandidateActions(tags);
        double bestScore = 0.5; //0.7
        int bestIndex = -1;
        String bestAction = "<none>";
        List<String> actionsList = mContextActionMap.getActions();
        for (int i: candidateActions) {
            String word = words.get(i);
            //ignore with/use words
            if (!(word.equals("use") || word.equals("with") || word.equals("using") ||
                    words.contains("utilise") ||
                    mContextActionMap.hasPossibleTarget(word) ||
                    mContextActionMap.hasPossibleContext(word)))
            {
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
                        if (deleteWord) { removeWordAtIndex(words, tags, i); }
                        return new Pair<>(i, action);
                    }
                }
                for (String action : actionsList) {
                    double score = SemanticSimilarity.getInstance().calculateScore(action, word);
                    if (score > 0.5 && score < 0.8) {
                        addAmbiguousCandidate(mAmbiguousActionCandidates,
                                new Triple<>(words.get(i), action, score), bestScore);
                    }
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                        bestAction = action;
                    }
                }
            }
        }

        if (bestIndex > -1) {
            if (bestScore > 0.5 && bestScore < 0.8) { mIsAmbiguous = true; }
            //Remove chosen action from list inputs
            if (deleteWord) { removeWordAtIndex(words, tags, bestIndex); }
        }

        return new Pair<>(bestIndex, bestAction);
    }

    private boolean useAltSlotFillingStructure(
            List<String> words, List<String> tags, int bestActionIndex)
    {
        List<String> keywords = Arrays.asList("use", "using", "with", "utilise");
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
        mAmbiguousTargetCandidates = new ArrayList<>();
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
                    if (score > 0.7 && score < 0.8) {
                        addAmbiguousCandidate(mAmbiguousTargetCandidates,
                                new Triple<>(words.get(i), target, score), bestScore);
                    }
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
            if (bestScore > 0.7 && bestScore < 0.8) { mIsAmbiguous = true; }
            removeWordAtIndex(words, tags, bestIndex);
            return bestTarget;
        }
    }

    private String getBestContext(List<String> words, List<String> tags, boolean usingAltSFS) {
        mAmbiguousContextCandidates = new ArrayList<>();
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
                    if (score > 0.6 && score < 0.8) {
                        addAmbiguousCandidate(mAmbiguousContextCandidates,
                                new Triple<>(words.get(i), context, score), bestScore);
                    }
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
            if (bestScore > 0.6 && bestScore < 0.8) { mIsAmbiguous = true; }
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
            if (!usingAltSFS && (words.get(i).equals("with") || words.get(i).equals("using") ||
                    words.get(i).equals("utilising"))) {
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
            } else if (words.get(i).equals("with") || words.get(i).equals("using")
                    || words.get(i).equals("utilising")) {
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

    private void addAmbiguousCandidate(
            List candidates, Triple triple, double bestScore) {
        if ((Double)(triple.third) > bestScore) {
            candidates.add(0, triple);
        } else {
            //Find insertion position
            boolean inserted = false;
            for (int i = 1; i < candidates.size(); ++i) {
                Triple candidate = (Triple)(candidates.get(i));
                if ((Double)(triple.third) > (Double)candidate.third) {
                    candidates.add(i, triple);
                    inserted = true;
                    break;
                }
            }
            if (!inserted) {
                candidates.add(triple);
            }
        }
    }

    public boolean isExpectingReply() { return mExpectingReply; }

    public void setExpectingReply(boolean mExpectingReply) {
        this.mExpectingReply = mExpectingReply;
    }
}
