package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ILexFile;
import edu.mit.jwi.item.IPointer;
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

    public int mHealth = 100;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mTagger = null;

        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;

        mInventory = new Inventory();
        mMap = new ItemActionMap(this);

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

            //check verb and check in look-up table for default action
            if (mMap.isValidAction(input)) {
                actionOutput =  mMap.get(input).get(0).run(this);
                acceptedAction = true;
            } else {
                actionOutput = "I didn't understand that.";
            }

            if (acceptedAction) {
                //It's the enemy's turn now
            }

            // Output new enemy status
            enemyOutput += mCurrentEnemy.mName + ": " + mCurrentEnemy.mHealth + " / " + mCurrentEnemy.mMaxHealth;

            return actionOutput + "\n\n" + enemyOutput;

        } else {    //mGameMode == MODE_OVERWORLD
            String overworldOutput = "";
        }

        return "None";
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
