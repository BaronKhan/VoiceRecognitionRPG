package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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

public class GameState {
    public Activity mMainActivity;

    public IDictionary mDict = null;
    MaxentTagger mTagger;

    // Environment Settings
    public enum Mode { MODE_OVERWORLD, MODE_BATTLE }

    public Inventory mInventory;
    public ItemActionMap mMap;

    public Mode mGameMode;
    public Enemy mCurrentEnemy;
    public Room mCurrentRoom;

    public String mActionContext;

    public int mPlayerHealth = 100;

    //Semantic Similarity Engine
    ILexicalDatabase mDb = null;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mTagger = null;

        mGameMode = MODE_OVERWORLD;

        Enemy.sGameState = this;
        mCurrentEnemy = null;

        mInventory = new Inventory();
        mMap = new ItemActionMap(this);

        WS4JConfiguration.getInstance().setMFS(false);  //Use all senses, not just most frequent sense (slower but more accurate)

        try {
            // Load model
            String modelPath = Environment.getExternalStorageDirectory()
                    + "/english-left3words-distsim.tagger";
            File modelFile = new File(modelPath);
            if (modelFile.exists()) {
                mTagger = new MaxentTagger(modelPath);
            }
        } catch (Exception e) {
            Toast.makeText(mainActivity, "Error loading model: " + e.getMessage(), Toast.LENGTH_LONG).show();
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

    public void initBattleState(Enemy currentEnemy) {
        mGameMode = MODE_BATTLE;
        mCurrentEnemy = currentEnemy;
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
                throw new AssertionError("Error: no. of words != no.of tags");
            }
            
            String chosenAction = getBestAction(words, tags);


            if (mMap.isValidAction(chosenAction)) {
                String chosenTarget = getBestTarget(words, tags);
                int actionIndex = getBestContext(words, tags, chosenAction.equals("use"));
                if (mMap.get(chosenAction).get(actionIndex) == null) {
                    actionOutput += "You cannot " + chosenAction + " with that item. Ignoring...\n";
                    actionIndex = 0;
                }
                if (mMap.get(chosenAction).get(actionIndex) == null) {
                    actionOutput += "Intent not understood.";
                } else {
                    actionOutput += mMap.get(chosenAction).get(actionIndex).run(this);
                    acceptedAction = true;
                }
            } else { actionOutput = "Intent not understood."; }

            if (acceptedAction) { enemyOutput += mCurrentEnemy.takeTurn() + "\n"; }

            enemyOutput += mCurrentEnemy.mName + "'s health: " + mCurrentEnemy.mHealth + " / " + mCurrentEnemy.mMaxHealth;
            playerOutput += "Your health: "+mPlayerHealth + " / " + 100;

            return actionOutput + "\n\n" + enemyOutput + "\n";

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
        Set<String> keys = mMap.mMap.keySet();
        String[] actionsList = keys.toArray(new String[keys.size()]);
        for (int i: candidateActions) {
            String word = words.get(i);
            for (String action : actionsList) {
                if (word.equals(action)) {
                    words.remove(i);
                    tags.remove(i);
                    return action;
                }
                else if (action != "use") {
                    double score = calculateScore(action, word);
                    if (score > bestScore) {
                        bestScore = score;
                        bestIndex = i;
                        bestAction = action;
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

    public String getBestTarget(List<String> words, List<String> tags) {
        if (mCurrentEnemy == null) { return null; }
        List<Integer> candidateTargets = getCandidateTargets(words, tags);
        String enemyName = mCurrentEnemy.mName;
        for (int i : candidateTargets) {
            String word = words.get(i);
            if (word.equals(enemyName)) {
                words.remove(i);
                tags.remove(i);
                return enemyName;
            }
        }
        return "<none>";
    }

    public int getBestContext(List<String> words, List<String> tags, boolean withUseAction) {
        List<String> candidateContext = getCandidateItems(words, tags, withUseAction);
        if (candidateContext.size() < 1) {
            mActionContext = null;
            return 0;
        }
        double bestScore = 0.8;
        int bestContext;
        Item bestItem = null;
        //Find best word
        for (String contextWord : candidateContext) {
            for (Item item : mInventory.mItems) {
                String itemName = item.getName();
                if (contextWord.equals(itemName)) {
                    bestScore = 1.0;
                    bestItem = item;
                    break;
                } else if (!contextWord.equals(mCurrentEnemy.mName)) {
                    double score = calculateScore(contextWord, itemName);
                    if (score > bestScore) {
                        bestScore = score;
                        bestItem = item;
                    }
                }
            }
        }
        //Determine type of word (index)
        if (bestItem == null) {
            return 0;
        } else {
            mActionContext = bestItem.getName();
            switch (bestItem.getType()) {
                case ITEM_WEAPON:
                    if (bestItem.itemIs("sharp")) {
                        bestContext = 2;    //AttackWeaponSharp
                    } else if (bestItem.itemIs("blunt")) {
                        bestContext = 3;    //AttackWeaponBlunt
                    } else {
                        bestContext = 1;    //AttackWeapon
                    }
                    break;
                case ITEM_HEALING:
                    bestContext = 4;    //HealItem
                    break;
                default:
                    bestContext = 0;    //default
                    break;
            }
        }
        return bestContext;
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

    public List<String> getCandidateItems(List<String> words, List<String> tags) {
        return getCandidateItems(words, tags, false);
    }

    public List<String> getCandidateItems(List<String> words, List<String> tags,
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
        Matcher m = Pattern.compile("(?<=_)[A-Z]+(?=\\s|$)").matcher(taggedWords);
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
