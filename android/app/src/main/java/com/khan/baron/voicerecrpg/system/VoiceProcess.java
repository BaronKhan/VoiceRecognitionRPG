package com.khan.baron.voicerecrpg.system;

import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.Triple;

public class VoiceProcess {
    private GlobalState mState;
    private ContextActionMap mContextActionMap;

    private Entity mActionContext; //Stores the name of the context

    private IDictionary mDict = null;

    //Made static to avoid out-of-space GC allocation errors
    private static MaxentTagger sTagger = null;

    private boolean mUsingAltSFS;

    private AmbiguousHandler mAmbiguousHandler;

    private boolean mExpectingMoreInput = false;
    private String mPreviousActionStr = null;
    private Entity mPreviousTarget = null;
    private Entity mPreviousContext = null;

    //Stored for action replies
    private Action mCurrentAction = null;

    public VoiceProcess(GlobalState state, ContextActionMap contextActionMap) {
        mState = state;
        mContextActionMap = contextActionMap;
        mAmbiguousHandler = new AmbiguousHandler(this);

        if (sTagger == null) {
            loadTagger();
        }
    }

    public static void loadTagger(String modelPath) {
        try {
            // Load model
            File modelFile = new File(modelPath);
            if (modelFile.exists()) {
                sTagger = new MaxentTagger(modelPath);
            }
        } catch (Exception e) {
            Log.e("VoiceProcess", "Error loading model: " + e.getMessage());
        }
    }

    public void loadTagger() {
        String modelPath = Environment.getExternalStorageDirectory()
                + "/english-left3words-distsim.tagger";
        loadTagger(modelPath);
    }

    public static MaxentTagger getTagger() {
        return sTagger;
    }

    public static void setTagger(MaxentTagger sTagger) {
        VoiceProcess.sTagger = sTagger;
    }

    public Object processInput(String input) {
        String actionOutput = "";
        if (mDict == null) { return "Error: WordNet not loaded."; }

        if (mAmbiguousHandler.isExpectingReply()) {
            return mAmbiguousHandler.processPendingIntent(input, mState, mContextActionMap);
        }

        mState.actionFailed();  // By default, we fail to execute input
        mActionContext = null;

        mAmbiguousHandler.resetState();

        // Process action reply
        if (mCurrentAction != null && mCurrentAction.wantsReply()) {
            actionOutput += mCurrentAction.processReply(mState, input);
            mCurrentAction.setWantsReply(false);
            return actionOutput;
        }

        // Tokenize and tag input
        List<String> words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        List<String> tags = getTags(input);
        removeContractions(words, tags);
        if (words.size()!=tags.size()) {
            throw new AssertionError("Error: no. of words("+words.size()
                    +") != no.of tags("+tags.size()+"), input = "+input);
        }

        // Elements in the words list are removed during this method, so keep a copy
        List<String> wordsCopy = new CopyOnWriteArrayList<>(words);

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
            } else {
                Object sentenceMatchResult = checkMatchingSentence(wordsCopy);
                if (sentenceMatchResult != null) { return sentenceMatchResult; }
                return "Intent not understood";
            }
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

            mPreviousContext = mActionContext;
            mPreviousTarget = currentTarget;

            if (mContextActionMap.isValidContext(chosenContext)) {
                if ((!chosenAction.equals("<none>")) &&
                        mContextActionMap.get(chosenContext).get(chosenAction) == null)
                {
                    actionOutput += "You cannot " + chosenAction + " with that. Ignoring...\n";
                    chosenContext = "default";
                }
            } else {
                chosenContext = "default";
            }

            if (mContextActionMap.get(chosenContext).get(chosenAction) == null) {
                Object sentenceMatchResult = checkMatchingSentence(wordsCopy);
                if (sentenceMatchResult != null) { return sentenceMatchResult; }
                actionOutput += "Intent not understood.";
            } else {
                mCurrentAction = mContextActionMap.get(chosenContext).get(chosenAction);
                //Check for ambiguous intent
                if (mAmbiguousHandler.isAmbiguous()) {
                    Object sentenceMatchResult = checkMatchingSentence(wordsCopy);
                    if (sentenceMatchResult != null) { return sentenceMatchResult; }

                    actionOutput += mAmbiguousHandler.initSuggestion(
                            chosenAction, currentTarget, chosenContext);
                } else {
                    mPreviousActionStr = chosenAction;
                    actionOutput += mCurrentAction.execute(mState, currentTarget);
                }
            }
        } else {
            //Copy words and tags
            List<String> oldWords = new CopyOnWriteArrayList<>(words);
            List<String> oldTags = new CopyOnWriteArrayList<>(tags);

            if (words.contains("use") || words.contains("with") || words.contains("using") ||
                    words.contains("utilise"))
            {
                String output = checkForAnotherTarget(words, tags);
                if (!output.equals("")) {
                    actionOutput += output;
                } else {
                    String currentContext = getBestContext(oldWords, oldTags, true);
                    // Check if using new context for previous action
                    if (mExpectingMoreInput) {
                        if (mContextActionMap.isValidContext(currentContext)) {
                            if (mContextActionMap.get(currentContext).get(mPreviousActionStr) == null) {
                                actionOutput = "You cannot " + mPreviousActionStr + " with that. Ignoring...\n";
                                currentContext = "default";
                            }
                        } else { currentContext = "default"; }
                        mCurrentAction = mContextActionMap.get(currentContext).get(mPreviousActionStr);
                        actionOutput += mCurrentAction.execute(mState, mPreviousTarget);
                    } else {
                        if (mActionContext != null) {
                            actionOutput = "What do you want to use the " + mActionContext.getName() + " for?";
                        } else {
                            Object sentenceMatchResult = checkMatchingSentence(wordsCopy);
                            if (sentenceMatchResult != null) {
                                return sentenceMatchResult;
                            }
                            actionOutput = "Intent not understood.";
                        }
                    }
                }
            } else {
                String output = checkForAnotherTarget(words, tags);
                if (!output.equals("")) {
                    actionOutput += output;
                } else {
                    String currentContext = getBestContext(oldWords, oldTags, true);
                    // Check if using new context for previous action
                    if (mExpectingMoreInput) {
                        if (mContextActionMap.isValidContext(currentContext)) {
                            if (mContextActionMap.get(currentContext).get(mPreviousActionStr) == null) {
                                actionOutput = "You cannot " + mPreviousActionStr + " with that. Ignoring...\n";
                                currentContext = "default";
                            }
                        } else {
                            currentContext = "default";
                        }
                        mCurrentAction = mContextActionMap.get(currentContext).get(mPreviousActionStr);
                        actionOutput += mCurrentAction.execute(mState, mPreviousTarget);
                    } else {
                        // Perform sentence matching as last resort
                        Object sentenceMatchResult = checkMatchingSentence(wordsCopy);
                        if (sentenceMatchResult != null) {
                            return sentenceMatchResult;
                        }
                        actionOutput = "Intent not understood.";
                    }
                }
            }
        }

        return actionOutput;
    }

    private String checkForAnotherTarget(List<String> words, List<String> tags) {
        String output = "";
        Entity possibleTarget = getBestTarget(words, tags, false);
        mActionContext = mPreviousContext;
        if (mExpectingMoreInput && possibleTarget != null
                && possibleTarget != mContextActionMap.mDefaultTarget
                && mPreviousActionStr != null) {
            String currentContext = getBestContext(words, tags, false);
            if (currentContext == null || currentContext.equals("default")) {
                if (mPreviousContext != null) { currentContext = mPreviousContext.getContext(); }
                else { currentContext = "default"; }
            }
            if (mContextActionMap.isValidContext(currentContext)) {
                if (mContextActionMap.get(currentContext).get(mPreviousActionStr) == null) {
                    output = "You cannot " + mPreviousActionStr + " with that. Ignoring...\n";
                    currentContext = "default";
                }
            } else { currentContext = "default"; }
            mCurrentAction = mContextActionMap.get(currentContext).get(mPreviousActionStr);
            output += mCurrentAction.execute(mState, possibleTarget);
        } else { return ""; }
        return output;
    }

    private Object checkMatchingSentence(List<String> words) {
        SentenceMapper sentenceMapper = mContextActionMap.getSentenceMapper();
        Triple<Action, String, List<String>> match = sentenceMapper.checkSentenceMatch(words);
        if (match != null) {
            //Check if target is available
            String targetName = match.second;
            if (mContextActionMap.hasPossibleTarget(targetName)) {
                Entity target = mContextActionMap.getPossibleTarget(targetName);
                mCurrentAction = match.first;
                return mCurrentAction.execute(mState, target);
            }
        }
        return null;
    }

    public String addSynonym(String synonym, String word) {
        mContextActionMap.addUserSynonym(synonym, word);
        return "Added synonym: " + synonym + " --> " + word + "\n";
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
        mAmbiguousHandler.initAmbiguousActionCandidates();
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
                    mContextActionMap.hasPossibleContext(word))) {
                if (mContextActionMap.hasSynonym(word)) {
                    if (deleteWord) { removeWordAtIndex(words, tags, i); }
                    return new Pair<>(i, mContextActionMap.getSynonymMapping(word));
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
                    if (mContextActionMap.wordIsIgnored(word, action)) { continue; }
                    double score = SemanticSimilarity.getInstance().calculateScore(action, word);
                    if (score > 0.5 && score < 0.8) {
                        mAmbiguousHandler.addAmbiguousActionCandidate(
                                new Triple<>(word, action, score), bestScore);
                    }
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                        bestAction = action;
                    }
                }
//                List<Double> scoresList = new CopyOnWriteArrayList<>();
//                actionsList.parallelStream().forEach((action) -> {
//                    if (!mContextActionMap.wordIsIgnored(word, action)) {
//                        double score = SemanticSimilarity.getInstance().calculateScore(action, word);
//                        scoresList.add(score);
//                    } else {
//                        scoresList.add(0.0);
//                    }
//                });
//                for (int j = 0; j < actionsList.size(); ++j) {
//                    double score = scoresList.get(j);
//                    String action = actionsList.get(j);
//                    if (score > 0.5 && score < 0.8) {
//                        mAmbiguousHandler.addAmbiguousActionCandidate(
//                                new Triple<>(word, action, score), bestScore);
//                    }
//                    if (score > bestScore) {
//                        bestScore = score;
//                        bestIndex = i;
//                        bestAction = action;
//                    }
//                }
            }
        }

        if (bestIndex > -1) {
            if (bestScore > 0.5 && bestScore < 0.8) { mAmbiguousHandler.setIsAmbiguous(true); }
            else { mAmbiguousHandler.clearAmbiguousActionCandidates(); }
            //Remove chosen action from list inputs
            if (deleteWord) { removeWordAtIndex(words, tags, bestIndex); }
        } else { mAmbiguousHandler.clearAmbiguousActionCandidates(); }

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
        mAmbiguousHandler.initAmbiguousTargetCandidates();
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
            if (mContextActionMap.hasSynonym(word)) {
                String targetName = mContextActionMap.getSynonymMapping(word);
                if (mContextActionMap.hasPossibleTarget(targetName)) {
                    removeWordAtIndex(words, tags, i);
                    return mContextActionMap.getPossibleTarget(targetName);
                }
            }
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
                    if (score > 0.7 && score < 0.81) {
                        mAmbiguousHandler.addAmbiguousTargetCandidate(
                                new Triple<>(word, target, score), bestScore);
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
            mAmbiguousHandler.clearAmbiguousTargetCandidates();
            return mContextActionMap.mDefaultTarget;
        } else {
            if (bestScore > 0.7 && bestScore < 0.8) { mAmbiguousHandler.setIsAmbiguous(true); }
            else { mAmbiguousHandler.clearAmbiguousTargetCandidates(); }
            removeWordAtIndex(words, tags, bestIndex);
            return bestTarget;
        }
    }

    private String getBestContext(List<String> words, List<String> tags, boolean usingAltSFS) {
        mAmbiguousHandler.initAmbiguousContextCandidates();
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
            if (mContextActionMap.hasSynonym(word)) {
                String contextName = mContextActionMap.getSynonymMapping(word);
                if (mContextActionMap.hasPossibleContext(contextName)) {
                    bestScore = 1.0;
                    bestContext = mContextActionMap.getPossibleContext(contextName);
                    bestIndex = i;
                    break;
                }
            }
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
                    if (score > 0.6 && score < 0.81) {
                        mAmbiguousHandler.addAmbiguousContextCandidate(
                                new Triple<>(words, context, score), bestScore);
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
            if (bestScore > 0.6 && bestScore < 0.8) { mAmbiguousHandler.setIsAmbiguous(true); }
            else { mAmbiguousHandler.clearAmbiguousContextCandidates(); }
            removeWordAtIndex(words, tags, bestIndex);
        } else { mAmbiguousHandler.clearAmbiguousContextCandidates(); }

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

    public ContextActionMap getContextActionMap() {
        return mContextActionMap;
    }

    public void setContextActionMap(ContextActionMap mContextActionMap) {
        this.mContextActionMap = mContextActionMap;
    }

    public boolean isUsingAltSFS() {
        return mUsingAltSFS;
    }

    public void setUsingAltSFS(boolean mUsingAltSFS) {
        this.mUsingAltSFS = mUsingAltSFS;
    }

    public void setActionContext(Entity mActionContext) {
        this.mActionContext = mActionContext;
    }

    public boolean isExpectingReply() {
        return mAmbiguousHandler.isExpectingReply();
    }

    public void setExpectingReply(boolean expectingReply) {
        mAmbiguousHandler.setExpectingReply(expectingReply);
    }

    public boolean isExpectingMoreInput() {
        return mExpectingMoreInput;
    }

    public void setExpectingMoreInput(boolean mExpectingMoreInput) {
        this.mExpectingMoreInput = mExpectingMoreInput;
    }

    public static boolean replyIsYes(String input) {
        return input.contains("yes") || input.contains("yeah") || input.contains("yup");
    }

    public static boolean replyIsNo(String input) {
        return input.contains("no") || input.contains("na") || input.contains("nope") ||
                input.contains("negative");
    }
}
