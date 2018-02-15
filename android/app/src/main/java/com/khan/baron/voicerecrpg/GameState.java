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
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
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

    public int mHealth = 100;

    //Semantic Similarity Engine
    ILexicalDatabase mDb = null;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mTagger = null;

        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;

        mInventory = new Inventory();
        mMap = new ItemActionMap(this);

        //Don't use Most frequent sense
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
            boolean acceptedAction = false;

            //tokenise and tag
            List<String> words = Arrays.asList(input.split(" "));
            List<String> tags = getTags(input);

            assert(words.size() == tags.size());

            //extract verbs and nouns for action
            List<String> candidateActions = getCandidateActions(words, tags);
            String chosenAction = getMostSimilarWord(candidateActions);

            //check verb and check in look-up table for default action
            if (mMap.isValidAction(chosenAction)) {
                // At this point, try to find item associated with action.
                List<String> candidateContext = getCandidateContext(words, tags, chosenAction.equals("use"));
                int actionIndex = getActionContext(candidateContext);
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
            } else {
                actionOutput = "Intent not understood.";
            }

            if (acceptedAction) { //TODO: It's the enemy's turn now
            }

            // Output new enemy status
            enemyOutput += mCurrentEnemy.mName + "'s health: " + mCurrentEnemy.mHealth + " / " + mCurrentEnemy.mMaxHealth;

            return actionOutput + "\n\n" + enemyOutput;

        } else {    //mGameMode == MODE_OVERWORLD
            String overworldOutput = "";
        }

        return "None";
    }

    public int getActionContext(List<String> candidateContext) {
        if (candidateContext.size() < 1) {
            mActionContext = null;
            return 0;
        }
        double bestScore = 0.8;
        int bestContext;
        Item bestItem = null;
        boolean foundWithUsing = false;
        //Find best word
        for (String contextWord : candidateContext) {
            for (Item item : mInventory.mItems) {
                String itemName = item.getName();
                if (contextWord.equals(itemName)) {
                    bestScore = 1.0;
                    bestItem = item;
                    break;
                } else {
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

    public String getMostSimilarWord(List<String> words) {
        double bestScore = 0.8;
        int bestIndex = -1;
        String bestAction = "<none>";
        Set<String> keys = mMap.mMap.keySet();
        String[] actionsList = keys.toArray(new String[keys.size()]);
        for (int i=0; i<words.size(); ++i) {
            String word = words.get(i);
            for (String action : actionsList) {
                if (word.equals(action)) {
                    words.remove(i);
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

        //Remove chosen word from list input
        if (bestIndex > -1) {
            words.remove(bestIndex);
        }

        return bestAction;
    }

    public double calculateScore(String word1, String word2) {
        if (mDb == null) {
            Toast.makeText(mMainActivity, "Error while parsing: ILexicalDatabase not loaded", Toast.LENGTH_LONG).show();
            return 0.0;
        }
        double score;
        try {
            score = new WuPalmer(mDb).calcRelatednessOfWords(word1, word2);
        } catch (Exception e) {
            Toast.makeText(mMainActivity, "Error while parsing: Unsupported POS Pairs", Toast.LENGTH_LONG).show();
            score = 0.0;
        }
        return score;
    }

    public List<String> getCandidateActions(List<String> words, List<String> tags) {
        List<String> candidateActions = new ArrayList<>();
        for (int i=0; i<words.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (tag.charAt(0) == 'v' || tag.charAt(0) == 'n') {
                candidateActions.add(words.get(i));
            }
        }
        return candidateActions;
    }

    public List<String> getCandidateContext(List<String> words, List<String> tags) {
        return getCandidateContext(words, tags, false);
    }

    public List<String> getCandidateContext(List<String> words, List<String> tags, boolean withUseAction) {
        List<String> candidateActions = new ArrayList<>();
        boolean foundWithUsing = withUseAction;
        for (int i=0; i<words.size(); ++i) {
            String tag = tags.get(i).toLowerCase();
            if (foundWithUsing &&
                    (tag.charAt(0) == 'v' || tag.charAt(0) == 'n' || tag.charAt(0) == 'j')) {
                candidateActions.add(words.get(i));
            } else if (words.get(i).equals("with") || words.get(i).equals("using")) {
                foundWithUsing = true;
            }
        }
        //remove everything before with/using
        return candidateActions;
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
}
