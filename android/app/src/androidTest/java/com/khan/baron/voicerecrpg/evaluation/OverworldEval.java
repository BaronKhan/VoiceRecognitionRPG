package com.khan.baron.voicerecrpg.evaluation;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.items.Potion;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.rooms.Room;
import com.khan.baron.voicerecrpg.game.rooms.Room01;
import com.khan.baron.voicerecrpg.game.rooms.RoomMoreUtensils;
import com.khan.baron.voicerecrpg.game.rooms.RoomUtensil;
import com.khan.baron.voicerecrpg.system.AmbiguousHandler;
import com.khan.baron.voicerecrpg.system.ContextActionMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OverworldEval {
    private GameState gameState;
    private URL mUrl;

    public OverworldEval() {
        gameState = new GameState(null);
        ContextActionMap.setRememberUserSynonyms(false);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                mUrl = new URL("file", null, dictFile.getPath());
                gameState.addDictionary(mUrl);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    private void testInventoryShown(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("your inventory"), correctInput);
    }

    private void testLookRoom01(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input)
                .contains("in a room with a locked door"), correctInput);
    }

    private void testScratched(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("scratched"), correctInput);
    }

    @Test
    public void testInventorySuite() {
        gameState.initOverworldState(new Room01());
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("show my inventory", true);
        testInventoryShown("show my troll", false);
        gameState.updateState("no");   //previous intent was misunderstood
        testInventoryShown("show my bag", true);
        testInventoryShown("please show the contents of my possessions that I have", true);
    }

    @Test
    public void testLookSuite() {
        gameState.initOverworldState(new Room01());
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("look at my inventory", true);
        testInventoryShown("look inside the bag", true);
        testLookRoom01("look around", true);
        testLookRoom01("look at the surroundings", true);
        testLookRoom01("observe the room", true);
    }

    @Test
    public void testPickUpSuite() {
        gameState.initOverworldState(new Room01()); // Only one knife in the room
        EvaluateMechanisms.runTest(gameState.updateState("pick up the door").contains("picked up"), false);
        EvaluateMechanisms.runTest(gameState.updateState("pick up the knife")
                .contains("picked up the knife"), true);
        EvaluateMechanisms.runTest(gameState.updateState("pick up the knife")
                .contains("picked up the knife"), false);
        EvaluateMechanisms.runTest(gameState.updateState("grab the potion")
                .contains("picked up the potion"), true);
    }

    @Test
    public void testRoom01CutSuite() {
        gameState.initOverworldState(new Room01());
        gameState.updateState("pick up the knife");
        EvaluateMechanisms.runTest(gameState.updateState("cut the painting using the knife")
                .contains("cut the painting"), true);
        EvaluateMechanisms.runTest(gameState.updateState("cut the painting using the knife")
                .contains("already"), true);
        testScratched("cut the table using the knife", true);
        testScratched("slash the door using the knife", true);
    }

    @Test
    public void testBreakSuite() {
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("break the table").contains("broke the table"), true);
        EvaluateMechanisms.runTest(gameState.updateState("break the table").contains("broke the table"), false);
        EvaluateMechanisms.runTest(gameState.updateState("break the door")
                .contains("cannot break the door"), true);
    }

    @Test
    public void testOpenSuite() {
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("open the table").contains("opened the drawer"), true);
        gameState.updateState("open the door");
        EvaluateMechanisms.runTest(gameState.updateState("yes").contains("locked"), true);
        gameState.updateState("pick up the knife");
        gameState.updateState("cut the painting");
        gameState.updateState("open the door");
        EvaluateMechanisms.runTest(gameState.updateState("yes")
                .contains("opened"), true);
    }

    @Test
    public void testUseSuite() {
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("use your hand to pick up the knife")
                .contains("picked"), true);
        testScratched("use the knife to slash the door", true);
        testScratched("use the knife to cut the glass table", true);
        EvaluateMechanisms.runTest(gameState.updateState("use the knife to cut the painting")
                .contains("cut the painting"), true);
        gameState.updateState("use the key to open the door");
        EvaluateMechanisms.runTest(gameState.updateState("yeah")
                .contains("opened"), true);
    }

    @Test
    public void testShowActionsSuite() {
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("show my commands")
                .contains("following actions"), true);
        EvaluateMechanisms.runTest(gameState.updateState("take a look at my actions")
                .contains("following actions"), true);
    }

    @Test
    public void testAdaptiveLearningSuite() {
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("cut the utensil").contains("you mean"), true);
        gameState.updateState("yes");
        EvaluateMechanisms.runTest(gameState.updateState("cut the utensil").contains("you mean"), true);
        gameState.updateState("yes");
        EvaluateMechanisms.runTest(gameState.updateState("cut the utensil").contains("you mean"), false);
    }


    @Test
    public void testMultipleCommandsSuite() {
        gameState.initOverworldState(new Room01());
        gameState.updateState("grab the knife and cut the painting then unlock the door");
        EvaluateMechanisms.runTest(gameState.updateState("yeah").contains("opened"), true);
    }

    @Test
    public void testMultipleSuggestionsSuite() {
        AmbiguousHandler.setGiveMultipleSuggestions(true);
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("grab the utensil")
                .contains("you mean, \"grab"), true);   // the table
        EvaluateMechanisms.runTest(gameState.updateState("no")
                .contains("you mean, \"grab"), true);   // must be the knife next
        EvaluateMechanisms.runTest(gameState.updateState("yes")
                .contains("picked up the knife"), true);
    }

    @Test
    public void testMultipleCommandsAndMultipleSuggestionsSuite() {
        AmbiguousHandler.setGiveMultipleSuggestions(true);
        gameState.initOverworldState(new Room01());
        EvaluateMechanisms.runTest(gameState.updateState("grab the utensil and then cut the painting with the knife")
                .contains("you mean, \"grab"), true);   // the table
        EvaluateMechanisms.runTest(gameState.updateState("no")
                .contains("you mean, \"grab"), true);   // must be the knife next
        EvaluateMechanisms.runTest(gameState.updateState("yes")
                .contains("cut the painting"), true);
    }

    @Test
    public void testSentenceMatchingSuite() {
        gameState.initOverworldState(new Room01());
        gameState.getInventory().add(new Weapon("sword", "sharp", "long", "metallic"));
        EvaluateMechanisms.runTest(gameState.updateState("what are my actions")
                .contains("following actions"), true);
        EvaluateMechanisms.runTest(gameState.updateState("what actions can i do")
                .contains("following actions"), true);
        testInventoryShown("what is in my bag", true);
        testInventoryShown("hello world", false);
    }

    @Test
    public void testMultipleTargetsSuite() {
        Room testRoom = new Room01();
        gameState.initOverworldState(testRoom);
        EvaluateMechanisms.runTest(gameState.updateState("pick up the knife and the potion")
                .contains("picked up the potion"), true);
        EvaluateMechanisms.runTest(gameState.updateState("break the table and the door")
                .contains("cannot break the door"), true);
    }

    @Test
    public void testMultipleTargetsReversedSuite() {
        Room testRoom = new Room01();
        gameState.initOverworldState(testRoom);
        EvaluateMechanisms.runTest(gameState.updateState("pick up the potion and the knife")
                .contains("picked up the knife"), true);
        EvaluateMechanisms.runTest(gameState.updateState("break the door and the table")
                .contains("broke the table"), true);
    }

    @Test
    public void testMultipleTargetsWithContextSuite() {
        Room testRoom = new Room01();
        gameState.initOverworldState(testRoom);
        gameState.getInventory().add(new Weapon("sword", "sharp", "long", "metallic"));
        gameState.updateState("grab the knife");
        EvaluateMechanisms.runTest(gameState.updateState("use the knife to cut the table and the door")
                .contains("scratched the door using your knife"), true);
        EvaluateMechanisms.runTest(gameState.updateState("cut the table with the knife and the door with the sword")
                .contains("scratched the door using your sword"), true);
        EvaluateMechanisms.runTest(gameState.updateState("cut the door with the knife and then with the sword")
                .contains("scratched the door using your sword"), true);
    }

    @Test
    public void testMultipleSynonymMapping() throws IOException {
        ContextActionMap.setRememberUserSynonyms(true);
        ContextActionMap.addUserSynonymOnly("utensil", "knife");
        ContextActionMap.addUserSynonymOnly("utensil", "fork");
        gameState = new GameState(null);
        gameState.addDictionary(mUrl);
        Room testRoom = new RoomUtensil();
        gameState.initOverworldState(testRoom);

        String output = gameState.updateState("grab the utensil");
        EvaluateMechanisms.runTest(output, output.contains("you mean, \"grab"), true);   // the fork
        EvaluateMechanisms.runTest(gameState.updateState("no").contains("you mean, \"grab"), true);   // the knife
        EvaluateMechanisms.runTest(gameState.updateState("no").contains("No more suggestions"), true);

        EvaluateMechanisms.runTest(gameState.updateState("pick up the utensil").contains("you mean, \"grab"), true);
        EvaluateMechanisms.runTest(gameState.updateState("yes").contains("picked"), true);

        ContextActionMap.getUserSynonyms().clear();
        ContextActionMap.setRememberUserSynonyms(false);
    }

    @Test
    public void testMultipleUtensils() throws IOException {
        ContextActionMap.setRememberUserSynonyms(true);
        ContextActionMap.addUserSynonymOnly("utensil", "knife");
        ContextActionMap.addUserSynonymOnly("utensil", "fork");
        ContextActionMap.addUserSynonymOnly("utensil", "spoon");
        ContextActionMap.addUserSynonymOnly("utensil", "instrument");
        gameState = new GameState(null);
        gameState.addDictionary(mUrl);
        Room testRoom = new RoomMoreUtensils();
        gameState.initOverworldState(testRoom);

        String output = gameState.updateState("grab the utensil");
        EvaluateMechanisms.runTest(output, output.contains("Multiple matches"), true);
        output = gameState.updateState("pick up the utensil");
        EvaluateMechanisms.runTest(output, output.contains("Multiple matches"), true);

        ContextActionMap.getUserSynonyms().clear();
        ContextActionMap.setRememberUserSynonyms(false);
    }
}
