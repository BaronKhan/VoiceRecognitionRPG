import com.khan.baron.vcw.Pair;
import com.khan.baron.vcw.VoiceProcess;
import com.opencsv.CSVWriter;
import game.GameState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter("results.csv"));

//        String [] record = "3,David,Feezor,USA,40".split(",");
//        writer.writeNext(record);

        File dictFile = new File("../w/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database");
        } else {
            System.out.println("Could not find WordNet database. Path = "+dictFile);
            writer.close();
            System.exit(0);
        }
        URL url = new URL("file", null, dictFile.getPath());

        VoiceProcess.loadTagger("../w/english-left3words-distsim.tagger");

        GameState gameState = new GameState();
        try {
            gameState.addDictionary(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        String[] labels = {"test string", "expected", "result", "debug?"};
        writer.writeNext(labels);

        for (Pair<String, String> test : sGameTests) {
            String input = test.first;
            String expected = test.second;
            String result = gameState.updateState(input).replace("\n", " >> ");
            result += " >>> "+gameState.updateState("yes").replace("\n", " >> ");
            String[] record = {
                    input,
                    expected,
                    (result.contains(expected) ? "PASS" : "FAIL"),
                    (result.contains(expected) ? "" : result)
            };
            writer.writeNext(record);
        }

        writer.close();
    }

    public static List<Pair<String, String>> sGameTests = new ArrayList<Pair<String, String>>(Arrays.asList(
            new Pair<String, String>("attack", "ATTACK"),
            new Pair<String, String>("charge", "ATTACK"),
            new Pair<String, String>("hit", "ATTACK"),
            new Pair<String, String>("tackle", "ATTACK"),
            new Pair<String, String>("fight", "ATTACK"),
            new Pair<String, String>("assault", "ATTACK"),
            new Pair<String, String>("launch an assault", "ATTACK"),
            new Pair<String, String>("attack with a sword", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something sharp", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something pointy", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something long", "ATTACK_WEAPON"),
            new Pair<String, String>("attack with something metallic", "ATTACK_WEAPON"),
            new Pair<String, String>("heal", "HEAL"),
            new Pair<String, String>("recover", "HEAL"),
            new Pair<String, String>("regenerate", "HEAL"),
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
