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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
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
//        return testHypernyms(input);
//        return testCustomWordNet();

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
                // For now, just call default action
                List<String> candidateContext = getCandidateContext(words, tags, chosenAction.equals("use"));
                int actionIndex = getActionContext(candidateContext);

//                actionOutput += "action: " + chosenAction + ", context: " + actionIndex + "\n";

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

            if (acceptedAction) {
                //It's the enemy's turn now

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
        //First look for "with" or using preceding the context
        //Go through Inventory and objects near to player in the room.
        //Use one with the highest context to one of the words.
        //Then match with indices based on type of object, semantics, etc.
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

//                //check descriptions
//                if (bestScore < 0.5) {
//                    for (String descr : item.mDescription) {
//                        if (contextWord.equals(descr)) {
//                            bestScore = 1.0;
//                            bestItem = item;
//                        } else {
//                            double score = calculateScore(contextWord, descr);
//                            if (score > bestScore) {
//                                bestScore = score;
//                                bestItem = item;
//                            }
//                        }
//                    }
//                }
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
//            double scoreWup = new WuPalmer(mDb).calcRelatednessOfWords(word1, word2);
//            double scoreLesk = new Lesk(mDb).calcRelatednessOfWords(word1, word2);
//            score = (scoreWup + (scoreLesk/16.0))/2.0;
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

    ////////////////////
    // TEST FUNCTIONS //
    ////////////////////

    public String testHypernyms(String input) {
        try {
            // extract noun/verb from sentence
            String tagged = null;
            if (mTagger != null) {
                tagged = mTagger.tagString(input);
            } else {
                return "Error: unable to load POS tagger model";
            }

            String taggedList[] = null;

            if (tagged != null) {
                taggedList = tagged.split(" ");
            }

            POS tagType = POS.NOUN;

            if (taggedList != null) {
                for (String i : taggedList) {
                    if (i.contains("_NN")) {
                        input = i.replace("_NN", "");
                        break;
                    } else if (i.contains("_VB")) {
                        input = i.replace("_VB", "");
                        tagType = POS.VERB;
                        break;
                    }
                }
            }

            // get first verb or noun
            IIndexWord idxWord = mDict.getIndexWord(input, tagType);
            IWordID wordID = idxWord.getWordIDs().get(0);
            IWord word = mDict.getWord(wordID);
            ISynset synset = word.getSynset();

            // get synonyms
            String output = tagged + "\n\nsynonyms: \n";
            for (IWord w : synset.getWords()) {
                output += w.getLemma() + ", ";
            }

            // get hypernyms
            output += "\n\nhypernyms: \n";
            List<ISynsetID> hypernymsList = synset.getRelatedSynsets(Pointer.HYPERNYM);
            for (ISynsetID sid : hypernymsList) {
                List<IWord> words = mDict.getSynset(sid).getWords();
                output += "{";
                for (Iterator<IWord> i = words.iterator(); i.hasNext(); ) {
                    output += i.next().getLemma() + ", ";
                }
                output += "}, ";
            }



            // print out the first hypernym branch of the word
            String currentWord = input;
            output += "\n\nhypernym branch: \n" + currentWord + " --> ";
            for (int i = 0; i < 5; ++i) {
                IIndexWord idxWord2 = mDict.getIndexWord(currentWord, tagType);
                IWordID wordID2 = idxWord2.getWordIDs().get(0);
                IWord word2 = mDict.getWord(wordID2);
                ISynset synset2 = word2.getSynset();
                List<ISynsetID> hypernymsList2 = synset2.getRelatedSynsets(Pointer.HYPERNYM);
                List<IWord> words = mDict.getSynset(hypernymsList2.get(0)).getWords();
                if (words.size() > 0) {
                    currentWord = words.get(0).getLemma();
                    output += currentWord + " --> ";
                } else {
                    break;
                }
            }
            return output;
        } catch (Exception e) {
            return "input = " + input + "\n\nError: " + e.getMessage();
        }
    }

    double testCompute(ILexicalDatabase db, String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
        return s;
    }

    public String testCustomWordNet() {
        String output = "";
        ILexicalDatabase db = new CustomWordNet(mDict);

//        String word1 = "create", word2 = "extraterrestrial";
//        double distance = compute(db, word1, word2);
//        output += word1+ " - "+ word2 +" = " + distance+"\n";

        String[] words = {"add", "get", "filter", "remove", "check", "find", "collect", "create"};

        for(int i=0; i<words.length-1; i++){
            for(int j=i+1; j<words.length; j++){
                double distance = testCompute(db, words[i], words[j]);
                output += /*words[i] +" -  " +  words[j] + " = " + */distance+"\n";
            }
        }


//        Concept c = db.getMostFrequentConcept("dog", "n");	//returns different synsets with id
//        String cStr = c.toString();
//        output += "\nmost frequent:\n"+cStr+"\n";
//
//        Collection<Concept> cColl = db.getAllConcepts("dog", "n");
//        Iterator<Concept> iterator = cColl.iterator();
//        while (iterator.hasNext()) {
//            output += iterator.next().toString()+"\n";
//            iterator.next();
//        }
//
//        output += "\nall hypernyms of 02086723-n:";
//        Collection<String> hypernyms = db.getHypernyms("02086723-n");
//        Iterator<String> iterator2 = hypernyms.iterator();
//        while (iterator2.hasNext()) {
//            output += iterator2.next()+"\n";
//        }

        return output;
    }
}
