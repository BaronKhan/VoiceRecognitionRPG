package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.os.Environment;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class GameState {
    public Activity mMainActivity;

    public IDictionary mDict = null;
    MaxentTagger mTagger;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mTagger = null;

        // Load model
        String modelPath = Environment.getExternalStorageDirectory()
                + "/english-left3words-distsim.tagger";
        File modelFile = new File(modelPath);
        if (modelFile.exists()) {
            mTagger = new MaxentTagger(modelPath);
        }
    }

    public String updateState(String input) {
        //return testHypernyms(input);
        return "None";
    }

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

}
