package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.item.Synset;

/**
 * Created by Baron on 20/01/2018.
 */


// NictWordNet does not work because it uses some sqlite rubbish that doesn't work in Android.
// This is a custom class that attempts to implement the same thing using MIT's JWI library.
public class CustomWordNet implements ILexicalDatabase {
    public IDictionary mDict;
    public Map<String, ISynset> synsetMap = new HashMap<>();

    public static POS stringToTag(String posStr) {
        if (posStr == "n") { return POS.NOUN; }
        else if (posStr == "v") { return POS.VERB; }
        else if (posStr == "r") { return POS.ADVERB; }
        else if (posStr == "a") { return POS.ADJECTIVE; }
        else { return POS.NOUN; }
    }

    public CustomWordNet(IDictionary dict) {
        mDict = dict;
    }

    public Collection<Concept> getAllConcepts(String word, String pos) {
        List<Concept> synsetStrings = new ArrayList<>();
        IIndexWord idxWord = mDict.getIndexWord(word, stringToTag(pos));
        if (idxWord != null) {
            List<IWordID> wordIDs = idxWord.getWordIDs();
            for (IWordID id : wordIDs) {
                String synsetStr = id.getSynsetID().toString().replace("SID-", "").toLowerCase();
                synsetStrings.add(new Concept(synsetStr, edu.cmu.lti.jawjaw.pobj.POS.valueOf(pos)));
                IWord w = mDict.getWord(id);
                synsetMap.put(synsetStr, w.getSynset());
            }
        }
        return synsetStrings;
    }

    public Concept getMostFrequentConcept(String word, String pos) {
        Collection<Concept> concepts = getAllConcepts(word, pos);
        if (concepts.size() > 0) { return concepts.iterator().next(); }
        return null;
    }

    public Collection<String> getHypernyms(String synsetStr) {
        List<String> synsetStrings = new ArrayList<>();
        ISynset synset = synsetMap.get(synsetStr);
        if (synset != null) {
            List<ISynsetID> hypernymsList = synset.getRelatedSynsets(Pointer.HYPERNYM);

            for (ISynsetID id : hypernymsList) {
                String hypernymStr = id.toString().replace("SID-", "").toLowerCase();
                //Need to add this synset to the map
                List<IWord> words = mDict.getSynset(id).getWords();
                for (IWord w : words) {
                    synsetMap.put(hypernymStr, w.getSynset());
                }
                synsetStrings.add(hypernymStr);
            }
        }
        return synsetStrings;
    }

    public Concept findSynsetBySynset( String synset ) {
        return null;
    }

    //to offset.
    public String conceptToString( String synset ) {
        return null;
    }

    public Collection<String> getGloss( Concept synset, String linkString ) {
        return null;
    }
}
