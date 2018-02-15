package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jwi.Dictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GameStateTest {
    public GameState gameState;

    public GameStateTest() {
        gameState = new GameState(null);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                URL url = new URL("file", null, dictFile.getPath());
                gameState.mDict = new Dictionary(url);
                gameState.mDict.open();
                gameState.mDb = new CustomWordNet(gameState.mDict);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.khan.baron.voicerecrpg", appContext.getPackageName());
    }

    @Test
    public void testAttackInput() {
        gameState.initBattleState(new Troll(9999999));
        assertEquals(gameState.updateState("attack").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("attack with everything you've got").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("use").contains("attacked"), false);
        assertEquals(gameState.updateState("hit").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("heal").contains("attacked"), false);
        assertEquals(gameState.updateState("launch an assault").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("charge at the troll").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("jump up and down").contains("You attacked the troll."), false);
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        assertEquals(gameState.updateState("attack with the hammer").contains("hammer"), true);
        assertEquals(gameState.updateState("attack with the sledgehammer").contains("hammer"), true);
        assertEquals(gameState.updateState("attack the troll").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("hit the troll").contains("You attacked the troll."), true);
    }

    @Test
    public void testHealInput() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("elixer"));
        gameState.mInventory.add(new Weapon("sword"));
        assertEquals(gameState.updateState("heal").contains("healed"), true);
        assertEquals(gameState.updateState("use a potion").contains("healed"), true);
        assertEquals(gameState.updateState("hit").contains("healed"), false);
        assertEquals(gameState.updateState("heal with elixer").contains("healed"), true);
    }

    @Test
    public void testHypernyms() {
        String input = "dog";

        try {
            // extract noun/verb from sentence
            String tagged = null;
            if (gameState.mTagger != null) {
                tagged = gameState.mTagger.tagString(input);
            } else {
                System.out.println("Error: unable to load POS tagger model");
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
            IIndexWord idxWord = gameState.mDict.getIndexWord(input, tagType);
            IWordID wordID = idxWord.getWordIDs().get(0);
            IWord word = gameState.mDict.getWord(wordID);
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
                List<IWord> words = gameState.mDict.getSynset(sid).getWords();
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
                IIndexWord idxWord2 = gameState.mDict.getIndexWord(currentWord, tagType);
                IWordID wordID2 = idxWord2.getWordIDs().get(0);
                IWord word2 = gameState.mDict.getWord(wordID2);
                ISynset synset2 = word2.getSynset();
                List<ISynsetID> hypernymsList2 = synset2.getRelatedSynsets(Pointer.HYPERNYM);
                List<IWord> words = gameState.mDict.getSynset(hypernymsList2.get(0)).getWords();
                if (words.size() > 0) {
                    currentWord = words.get(0).getLemma();
                    output += currentWord + " --> ";
                } else {
                    break;
                }
            }
            System.out.println(output);
        } catch (Exception e) {
            System.out.println("input = " + input + "\n\nError: " + e.getMessage());
        }
    }

    double testCompute(ILexicalDatabase db, String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(true);
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
        return s;
    }

    @Test
    public void testCustomWordNet() {
        String output = "";
        ILexicalDatabase db = new CustomWordNet(gameState.mDict);

        String[] words = {"add", "get", "filter", "remove", "check", "find", "collect", "create"};

        for(int i=0; i<words.length-1; i++){
            for(int j=i+1; j<words.length; j++){
                double distance = testCompute(db, words[i], words[j]);
                output += words[i] +" -  " +  words[j] + " = " + distance+"\n";
            }
        }

        System.out.println(output);
    }
}
