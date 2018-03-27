package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room;

import java.io.File;
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
import edu.mit.jwi.IDictionary;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_BATTLE;
import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_OVERWORLD;

public class GameState extends GlobalState {
    public Activity mMainActivity;

    public IDictionary mDict = null;
    MaxentTagger mTagger;

    // Environment Settings
    public enum Mode { MODE_OVERWORLD, MODE_BATTLE }

    public Inventory mInventory;
    public ContextActionMap mBattleMap;

    public Mode mGameMode;
    public Enemy mCurrentEnemy;
    public Room mCurrentRoom;

    public Context mActionContext; //stores the name of the context

    public int mPlayerHealth = 100;

    //Semantic Similarity Engine
    ILexicalDatabase mDb = null;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mTagger = null;

        mGameMode = MODE_OVERWORLD;

        mCurrentEnemy = null;

        mBattleMap = new ContextActionMap(this);
        mInventory = new Inventory(mBattleMap);

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

    public void initState() {
        // for demo
        initBattleState(new Troll(100));
        mInventory.add(new Weapon("sword", "sharp", "long", "metallic"));
        mInventory.add(new Weapon("knife", "sharp", "short", "metallic"));
        mInventory.add(new Weapon("hammer", "heavy", "blunt"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("elixer"));
    }

    public void setCurrentEnemy(Enemy currentEnemy) {
        mCurrentEnemy = currentEnemy;
        mBattleMap.setPossibleTargets(new ArrayList<Context>(Arrays.asList(currentEnemy, mInventory)));
        mBattleMap.setDefaultTarget(mCurrentEnemy);
    }

    public void initBattleState(Enemy currentEnemy) {
        mGameMode = MODE_BATTLE;
        setCurrentEnemy(currentEnemy);
    }

    public void initOverworldState(Room room) {
        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;
        mCurrentRoom = room;
    }

    public String updateState(String input) {
        if (mDict == null) { return "Error: WordNet not loaded."; }

        String actionOutput = "";

        if (mGameMode == MODE_BATTLE) {
            String enemyOutput = "";
            String playerOutput = "";
            boolean acceptedAction = false;

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

            if (mBattleMap.isValidAction(chosenAction)) {
                Context currentTarget = getBestTarget(words, tags);
//                Toast.makeText(mMainActivity, "chosenTarget = " +currentTarget.getName(),
//                        Toast.LENGTH_LONG).show();
                String chosenContext = getBestContext(words, tags, chosenAction.equals("use"));
//                Toast.makeText(mMainActivity, "chosenContext = " +chosenContext,
//                        Toast.LENGTH_LONG).show();
                if (mBattleMap.isValidContext(chosenContext)) {
                    if (mBattleMap.get(chosenContext).get(chosenAction) == null) {
                        actionOutput += "You cannot " + chosenAction + " with that. Ignoring...\n";
                        chosenContext = "default";
                    }
                } else { chosenContext = "default"; }
                if (mBattleMap.get(chosenContext).get(chosenAction) == null) {
                    actionOutput += "Intent not understood.";
                } else {
                    actionOutput += mBattleMap.get(chosenContext).get(chosenAction).run(this, currentTarget);
                    acceptedAction = getActionSucceed();
                }
            } else { actionOutput = "Intent not understood."; }

            if (acceptedAction) { enemyOutput += mCurrentEnemy.takeTurn(this) + "\n"; }

            enemyOutput += mCurrentEnemy.getName() + "'s health: " + mCurrentEnemy.getHealth() +
                    " / " + mCurrentEnemy.getMaxHealth();
            playerOutput += "Your health: " + mPlayerHealth + " / " + 100;

            return actionOutput + "\n\n" + enemyOutput + " | " + playerOutput;

        } else {    //mGameMode == MODE_OVERWORLD
            String overworldOutput = "";
        }

        return "None";
    }

    public String getBestAction(List<String> words, List<String> tags) {
        List<Integer> candidateActions = getCandidateActions(tags);
        double bestScore = 0.8;
        int bestIndex = -1;
        String bestAction = "<none>";
        List<String> actionsList = mBattleMap.getActions();
        for (int i: candidateActions) {
            String word = words.get(i);
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

    public Context getBestTarget(List<String> words, List<String> tags) {
        List<Integer> candidateTargets = getCandidateTargets(words, tags);
        List<Context> possibleTargetList = mBattleMap.getPossibleTargets();
        if (candidateTargets == null || possibleTargetList == null ||
                candidateTargets.size() < 1 || possibleTargetList.size() < 1) {
            return mBattleMap.mDefaultTarget;
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
                    if (target.contextIs(word)) {
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
            return mBattleMap.mDefaultTarget;
        } else {
            words.remove(bestIndex);
            tags.remove(bestIndex);
            return bestTarget;
        }
    }

    public String getBestContext(List<String> words, List<String> tags, boolean withUseAction) {
        List<String> candidateContext = getCandidateContext(words, tags, withUseAction);
        List<Context> possibleContextList = mBattleMap.getPossibleContexts();
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
                } else if (!word.equals(mCurrentEnemy.getName())) {
                    if (context.contextIs(word)) {
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
        }
        //For the Context object, get its context string
        if (bestContext != null) {
            bestContextWord = bestContext.getContext();
            mActionContext = bestContext;
        }

        return bestContextWord;
    }

    public List<Integer> getCandidateActions(List<String> tags) {
        List<Integer> candidateActions = new ArrayList<>();
        for (int i=0; i<tags.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (tag.charAt(0) == 'v' || tag.charAt(0) == 'n') {
                candidateActions.add(i);
            }
        }
        return candidateActions;
    }

    public List<Integer> getCandidateTargets(List<String> words, List<String> tags) {
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

    public List<String> getCandidateContext(List<String> words, List<String> tags) {
        return getCandidateContext(words, tags, false);
    }

    public List<String> getCandidateContext(List<String> words, List<String> tags,
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

    public double calculateScore(String word1, String word2) {
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
//            Toast.makeText(mMainActivity, "wup = "+score1+". lin = "+score2, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(mMainActivity, "Error while parsing: Unsupported POS Pairs",
                    Toast.LENGTH_LONG).show();
            score = 0.0;
        }
        return score;
    }


    public List<String> getTags(String input) {
        assert(mTagger != null);
        String taggedWords = mTagger.tagString(input);
        List<String> tags = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=_)[A-Z$]+(?=\\s|$)").matcher(taggedWords);
        while (m.find()) {
            tags.add(m.group());
        }
        return tags;
    }

    public void removeContractions(List<String> words) {
        for (int i = 0; i < words.size(); ++i) {
            if (words.get(i).contains("'")) {
                words.remove(i);
            }
        }
    }
}
