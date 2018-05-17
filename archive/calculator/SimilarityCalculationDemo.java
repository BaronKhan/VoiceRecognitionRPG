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

public class SimilarityCalculationDemo {

	private static ILexicalDatabase db = new NictWordNet();
	/*
	//available options of metrics
	private static RelatednessCalculator[] rcs = { new HirstStOnge(db),
			new LeacockChodorow(db), new Lesk(db), new WuPalmer(db),
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db) };
	*/
	private static double compute(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(false);
		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
		return s;
	}

	public static void main(String[] args) {

    if (args.length == 2) {
        System.out.println("WUP: "+new WuPalmer(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("LIN: "+new Lin(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("JCN: "+new JiangConrath(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("LESK: "+new Lesk(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("LEA: "+new LeacockChodorow(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("HST: "+new HirstStOnge(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("RES: "+new Resnik(db).calcRelatednessOfWords(args[0], args[1]));
        System.out.println("PATH: "+new Path(db).calcRelatednessOfWords(args[0], args[1]));
    } else {

      System.out.println("Must specify two arguments.\nRunning default demo...")

		// String[] words = {"add", "get", "filter", "remove", "check", "find", "collect", "create", "dog", "cat"};
		String [] words = {"attack", "hit", "charge", "prevent", "use", "heal", "recover", "regenerate", "cure", "restore"};

		for(int i=0; i<words.length-1; i++){
			for(int j=i+1; j<words.length; j++){
				double distance = compute(words[i], words[j]);
				System.out.println(words[i] +" -  " +  words[j] + " = " + distance);
			}
		}

		//Testing output of ILexicalDatabase (db)
		Concept c = db.getMostFrequentConcept("dog", "n");	//returns different synsets with id
		String cStr = c.toString();
		System.out.println("\nmost frequent:\n"+cStr);

		Collection<Concept> cColl = db.getAllConcepts("dog", "n");
		Iterator<Concept> iterator = cColl.iterator();
		System.out.println("\nall concepts:");
        while (iterator.hasNext()) {
        	System.out.println(iterator.next().toString());
        }

        System.out.println("\nall hypernyms of 02084071-n:");
        Collection<String> hypernyms = db.getHypernyms("02084071-n");
        Iterator<String> iterator2 = hypernyms.iterator();
        while (iterator2.hasNext()) {
        	System.out.println(iterator2.next());
        }
      }
	}
}
