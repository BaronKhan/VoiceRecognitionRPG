import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import java.util.Collection;
import edu.cmu.lti.lexical_db.data.Concept;
import java.util.*;

public class Multithreading {

	private static ILexicalDatabase db = new NictWordNet();
	/*
	//available options of metrics
	private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
			new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
	*/
	private static double compute(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(false);
    try {
  		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
  		return s;
    }
    catch (Exception e) {
      System.out.println("Error while parsing: "+e.getMessage());
      return 0.0;
    }
	}

	public static void main(String[] args) {
    int count = 0;
    while (true) {
      double score = 0.0;
      List<String> sentences = Arrays.asList("cuttable", "scratchable", "2", "sentence", "lines", "words", "speech");
      score = sentences.parallelStream()
         .mapToDouble((sentence) -> compute("words", sentence))
         .sum();
      System.out.println(count+". score = "+score);
      ++count;
    }
  }
}
