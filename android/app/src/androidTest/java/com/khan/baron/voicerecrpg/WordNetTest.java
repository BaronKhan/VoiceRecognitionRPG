package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.game.BattleContextActionMap;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.CustomLexicalDatabase;
import com.khan.baron.voicerecrpg.system.SemanticSimilarity;
import com.khan.baron.voicerecrpg.system.VoiceProcess;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class WordNetTest {
    private GameState gameState;

    public WordNetTest() {
        gameState = new GameState(null);
        ContextActionMap.setRememberUserSynonyms(false);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                URL url = new URL("file", null, dictFile.getPath());
                gameState.addDictionary(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    @Test
    public void useAppContext() {
        // Entity of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("com.khan.baron.voicerecrpg", appContext.getPackageName());
    }

    @Test
    public void testSentenceMatchingSuite() {
        ContextActionMap map = new BattleContextActionMap(null);
        String input = "what is in my inventory";
        List<String> words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words).second, "inventory");

        input = "what actions can i do";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words).second, "actions");

        input = "what are my options";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words).second, "actions");

        input = "jump up and down";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words), null);

        input = "can you tell me what i can do please";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words).second, "actions");

        input = "fly away and never come back";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words), null);

        input = "attack the troll";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words), null);

        input = "grab the utensil";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words), null);

        input = "obliterate the enemy";
        words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        assertEquals(map.getSentenceMapper().checkSentenceMatch(words), null);
    }

    @Test
    public void testHypernyms() {
        String input = "dog";
        try {
            MaxentTagger tagger = VoiceProcess.getTagger();
            IDictionary dict = gameState.getBattleVoiceProcess().getDictionary();

            String tagged = null;
            if (tagger != null) {
                tagged = tagger.tagString(input);
            } else {
                assertEquals("Error: unable to load POS tagger model", true, false);
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
            } else { assertEquals("taggedList == null", true, false); }

            // get first verb or noun
            IIndexWord idxWord = dict.getIndexWord(input, tagType);
            IWordID wordID = idxWord.getWordIDs().get(0);
            IWord word = dict.getWord(wordID);
            ISynset synset = word.getSynset();

            // get synonyms
            StringBuilder output = new StringBuilder(tagged + "\n\nsynonyms: \n");
            for (IWord w : synset.getWords()) {
                output.append(w.getLemma()).append(", ");
            }

            // get hypernyms
            output.append("\n\nhypernyms: \n");
            List<ISynsetID> hypernymsList = synset.getRelatedSynsets(Pointer.HYPERNYM);
            for (ISynsetID sid : hypernymsList) {
                List<IWord> words = dict.getSynset(sid).getWords();
                output.append("{");
                for (IWord word1 : words) {
                    output.append(word1.getLemma()).append(", ");
                }
                output.append("}, ");
            }

            // print out the first hypernym branch of the word
            String currentWord = input;
            output.append("\n\nhypernym branch: \n").append(currentWord).append(" --> ");
            for (int i = 0; i < 5; ++i) {
                IIndexWord idxWord2 = dict.getIndexWord(currentWord, tagType);
                IWordID wordID2 = idxWord2.getWordIDs().get(0);
                IWord word2 = dict.getWord(wordID2);
                ISynset synset2 = word2.getSynset();
                List<ISynsetID> hypernymsList2 = synset2.getRelatedSynsets(Pointer.HYPERNYM);
                List<IWord> words = dict.getSynset(hypernymsList2.get(0)).getWords();
                if (words.size() > 0) {
                    currentWord = words.get(0).getLemma();
                    output.append(currentWord).append(" --> ");
                } else {
                    break;
                }
            }
            System.out.println(output);
        } catch (Exception e) {
            //If exception occurs, fail test
            assertEquals("Something went wrong when testing hypernyms: "+e.getMessage(), true, false);
        }
    }

    @Test
    public void testCustomLexicalDatabase() {
        try {
            StringBuilder output = new StringBuilder();
            ILexicalDatabase db = new CustomLexicalDatabase(gameState.getBattleVoiceProcess().getDictionary());
            SemanticSimilarity.getInstance().init(db);

            String[] words = {"add", "get", "filter", "remove", "check", "find", "collect", "create"};
            for (int i = 0; i < words.length - 1; i++) {
                for (int j = i + 1; j < words.length; j++) {
                    double distance = SemanticSimilarity
                            .getInstance()
                            .calculateScore(words[i], words[j]);
                    output.append(words[i]).append(" -  ").append(words[j]).append(" = ").append(distance).append("\n");
                }
            }

            System.out.println(output);
        } catch(Exception e) {
            assertEquals("Something wrong with CustomLexicalDatabase", true, false);
        }
    }
}
