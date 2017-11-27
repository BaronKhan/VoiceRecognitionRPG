package com.khan.baron.voicerecrpg;

import android.app.Activity;

import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class GameState {
    public Activity mMainActivity;

    public IDictionary mDict = null;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
    }

    public String updateState(String input) {
        // get nouns


        IIndexWord idxWord = mDict.getIndexWord ("dog", POS.NOUN );
        IWordID wordID = idxWord.getWordIDs().get(0);
        IWord word = mDict.getWord(wordID);
        return word.getSynset().getGloss ();
    }

}
