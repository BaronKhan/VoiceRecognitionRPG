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

public class PropertyChecker {

    private static ILexicalDatabase db = new NictWordNet();

    public static String[] properties = {
        "breakable",
        "scratch",  //scratchable
        "cut"   //cuttable
    };

    public static double threshold = 0.65;

    private static double compute(String word1, String word2) {
        WS4JConfiguration.getInstance().setMFS(false);
        double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
        return s;
    }

    public static void main(String[] args) {
        if (args.length >= 1) {
            String name = args[0];
            if (args.length == 3 && args[1].equals("--threshold")) {
                threshold = Double.parseDouble(args[2]);
            }
            if (threshold <= 0) {
                System.out.println("error while parsing threshold: "+args[2]);
                return;
            }
            for (String property: properties) {
                System.out.println(property+": "+
                    ((compute(name, property) > threshold)
                    ? "true" : "false"));
            }
            return;
        }
        System.out.println(
            "usage: <object_name> [--threshold <value>]");
    }
}
