import com.khan.baron.vcw.GlobalState;
import com.khan.baron.vcw.Pair;
import com.khan.baron.vcw.SemanticSimilarity;
import com.khan.baron.vcw.VoiceProcess;
import com.opencsv.CSVWriter;
import game.GameState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("results.csv"));

        VoiceProcess.loadTagger("../w/english-left3words-distsim.tagger");

        File dictFile = new File("../w/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database");
        } else {
            System.out.println("Could not find WordNet database. Path = "+dictFile);
            writer.close();
            System.exit(0);
        }
        URL url = new URL("file", null, dictFile.getPath());

        SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_WUP);

        GameState gameState = new GameState();
        try { gameState.addDictionary(url); }
        catch (Exception e) { System.out.println(e.getMessage()); }
        runTests(writer, gameState, "Game", sGameTests);

        writer.close();
    }

    private static void runTests(CSVWriter writer, GlobalState state, String testName, List<Pair<String, String>> tests) {
        writer.writeNext(new String[] {testName+":"});
        writer.writeNext(new String[] {"Test", "Expected", "Result", "Debug"});

        System.out.println("Running tests for "+testName);

        int scoreAcc = 0;
        long startTime = System.currentTimeMillis();
        for (Pair<String, String> test : tests) {
            String input = test.first;
            String expected = test.second;
            String result = state.updateState(input).replace("\n", " >> ");
            result += " >>> "+state.updateState("yes").replace("\n", " >> ");
            boolean passed = result.contains(expected);
            String[] gameRecord = {
                    input,
                    expected,
                    (passed ? "PASS" : "FAIL"),
                    (passed ? "" : result)
            };
            writer.writeNext(gameRecord);
            if (passed) { ++scoreAcc; }
            System.out.println("Result for "+testName+" test: "+input+" : "+gameRecord[2]);
        }
        long endTime = System.currentTimeMillis();

        long totalTime = (endTime - startTime);
        double score = (100.0 * scoreAcc / (double)tests.size());
        writer.writeNext(new String[] {
                "Time per test",
                new DecimalFormat("##.000").format((((double)totalTime)/tests.size()))+" ms",
                "Score:",
                new DecimalFormat("##.00").format(score)+"%"});
        System.out.println("Finished tests for "+testName);
        System.out.println("Total time = "+totalTime+" ms");
        System.out.println("Score = "+new DecimalFormat("##.00").format(score)+"%");
    }

    private static List<Pair<String, String>> sGameTests = new ArrayList<Pair<String, String>>(Arrays.asList(
            new Pair<String, String>("attack", "ATTACK"),
            new Pair<String, String>("charge", "ATTACK"),
            new Pair<String, String>("hit", "ATTACK"),
            new Pair<String, String>("tackle", "ATTACK"),
            new Pair<String, String>("fight", "ATTACK"),
            new Pair<String, String>("assault", "ATTACK"),
            new Pair<String, String>("battle", "ATTACK"),
            new Pair<String, String>("launch an assault", "ATTACK"),
            new Pair<String, String>("attack with a sword", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something sharp", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something pointy", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something long", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something metallic", "ATTACK_WEAPON"),
            new Pair<String, String>("heal", "HEAL"),
            new Pair<String, String>("recover", "HEAL"),
            new Pair<String, String>("regenerate", "HEAL"),
            new Pair<String, String>("rest", "HEAL"),
            new Pair<String, String>("heal with a potion", "HEAL_POTION"),
            new Pair<String, String>("heal with an elixer", "HEAL_POTION"),
            new Pair<String, String>("heal with an healing drink", "HEAL_POTION"),
            new Pair<String, String>("move forwards", "MOVE_FORWARDS"),
            new Pair<String, String>("move backwards", "MOVE_BACKWARDS"),
            new Pair<String, String>("move ahead", "MOVE_FORWARDS"),
            new Pair<String, String>("continue forwards", "MOVE_FORWARDS"),
            new Pair<String, String>("run forwards", "MOVE_FORWARDS"),
            new Pair<String, String>("dash forwards", "MOVE_FORWARDS")
    ));
}
