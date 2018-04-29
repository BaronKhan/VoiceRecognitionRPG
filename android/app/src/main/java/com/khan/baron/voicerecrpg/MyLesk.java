package com.khan.baron.voicerecrpg;

import android.util.Log;

import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.SynsetID;

/* IGNORE THIS CLASS!!!!!!!!!!!!!!! */
@Ignore
public class MyLesk extends RelatednessCalculator {
    protected static double min = 0.0D;
    protected static double max = 1.0D;
    private static List<POS[]> posPairs = new ArrayList<POS[]>() {
        {
            this.add(new POS[]{POS.n, POS.n});
            this.add(new POS[]{POS.v, POS.v});
        }
    };

    public MyLesk(ILexicalDatabase db) {
        super(db);
        Log.d("MyLesk", "Create MyLesk");
    }

    protected Relatedness calcRelatedness(Concept synset1, Concept synset2) {
        StringBuilder tracer = new StringBuilder();
        Log.d("MyLesk", synset1.getSynset() + ", " + synset2.getSynset());
        String synsetStr = synset1.getSynset();
        char posTag = synsetStr.charAt(synsetStr.length()-1);
        int synsetOffset = Integer.parseInt(synsetStr.replaceAll("-[a-zA-Z]", ""));
        Log.d("MyLesk", "offset: "+synsetOffset + ", tag = "+posTag);
        SynsetID id = new SynsetID(synsetOffset, edu.mit.jwi.item.POS.getPartOfSpeech(posTag));
        Log.d("MyLesk", id.toString());
        if (db instanceof CustomLexicalDatabase) {
            ISynset synsetJWI = ((CustomLexicalDatabase) db).mDict.getSynset(id);
            Log.d("MyLesk", "synsetJWI.gloss() = "+synsetJWI.getGloss());
        }
        return new Relatedness(0.0, null, null);
    }

    public List<POS[]> getPOSPairs() {
        return posPairs;
    }
}
