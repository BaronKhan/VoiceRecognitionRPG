package com.khan.baron.voicerecrpg;

import android.util.Log;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import static java.lang.Math.max;

public class SemanticSimilarity {
    public enum SimilarityMethod {
        METHOD_WUP_LIN,
        METHOD_WUP,
        METHOD_LIN,
        METHOD_JCN,
        METHOD_LESK
    }

    private static final SemanticSimilarity sInstance = new SemanticSimilarity();

    public static SemanticSimilarity getInstance() { return sInstance; }

    //Use all senses, not just most frequent sense (slower but more accurate)
    private SemanticSimilarity() { WS4JConfiguration.getInstance().setMFS(false); }

    private static SimilarityMethod sCurrentMethod = SimilarityMethod.METHOD_WUP_LIN;

    private ILexicalDatabase mDb = null;
    private RelatednessCalculator mMethod1 = null;
    private RelatednessCalculator mMethod2 = null;

    public void init(ILexicalDatabase db) {
        mDb = db;
        setSimilarityMethod(sCurrentMethod);
    }

    public static SimilarityMethod getSimilarityMethod() {
        return sCurrentMethod;
    }

    protected static void setStaticSimilarityMethod(SimilarityMethod chosenMethod) {
        sCurrentMethod = chosenMethod;
    }

    protected void setSimilarityMethod(SimilarityMethod chosenMethod) {
        sCurrentMethod = chosenMethod;
        if (mDb == null) { return; }
        mMethod1 = null; mMethod2 = null;
        switch(chosenMethod) {
            case METHOD_WUP_LIN:
                mMethod1 = new WuPalmer(mDb);
                mMethod2 = new Lin(mDb);
                break;
            case METHOD_WUP:
                mMethod1 = new WuPalmer(mDb);
                break;
            case METHOD_LIN:
                mMethod1 = new Lin(mDb);
                break;
            case METHOD_JCN:
                mMethod1 = new JiangConrath(mDb);
                break;
            case METHOD_LESK:
                mMethod1 = new Lesk(mDb);
                break;
            default:
                mMethod1 = new WuPalmer(mDb);
                mMethod2 = new Lin(mDb);
        }
    }

    public double calculateScore(String word1, String word2) {
        if (mDb == null) {
            Log.e("SemanticSimilarity", "Error while parsing: ILexicalDatabase not loaded",
                    new AssertionError());
            return 0.0;
        }

        double score1=0.0, score2=0.0, score;
        try {
            if (mMethod1 != null) { score1 = mMethod1.calcRelatednessOfWords(word1, word2); }
            if (mMethod2 != null) { score2 = mMethod2.calcRelatednessOfWords(word1, word2); }

            if (sCurrentMethod == SimilarityMethod.METHOD_WUP_LIN && score2 > 0)  {
                score = (score1*0.75) + (score2*0.25);
            } else { score = score1; }
            Log.d("SemanticSimilarity", "score("+word1+", "+word2+") = "+score);

            //Normalise score
            if (sCurrentMethod == SimilarityMethod.METHOD_LESK) {
                score = max(score / 80.0, 1.0);
            }

            return score;
        } catch (Exception e) {
            Log.e("SemanticSimilarity", "Error while parsing: "+e.getMessage(),
                    new AssertionError());
            return 0.0;
        }
    }

    //TODO: calculate cosine similarity of definitions
    private double calculateDefinitionCosSimilarity(String word1, String word2) { return 0.0; }
}
