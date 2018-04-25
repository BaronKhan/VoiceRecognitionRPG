package com.khan.baron.voicerecrpg;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room01;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OverworldTest {
    private GameState gameState;

    public OverworldTest() {
        gameState = new GameState(null);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                URL url = new URL("file", null, dictFile.getPath());
                gameState.addDictionary(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    private void testInventoryShown(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("your inventory"), correctInput);
    }

    private void testLookRoom01(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input)
                .contains("in a room with a locked door"), correctInput);
    }

    private void testScratched(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("scratched"), correctInput);
    }

    @Test
    public void testInventorySuite() {
        gameState.initOverworldState(new Room01());
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("show my inventory", true);
        testInventoryShown("show my troll", false);
        gameState.updateState("no");   //previous intent was misunderstood
        testInventoryShown("show my bag", true);
        testInventoryShown("please show the contents of my possessions that I have", true);
    }

    @Test
    public void testLookSuite() {
        gameState.initOverworldState(new Room01());
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("look at my inventory", true);
        testInventoryShown("look inside the bag", true);
        testLookRoom01("look around", true);
        testLookRoom01("look at the surroundings", true);
        testLookRoom01("observe the room", true);
    }

    @Test
    public void testPickUpSuite() {
        gameState.initOverworldState(new Room01()); // Only one knife in the room
        assertEquals(gameState.updateState("pick up the door").contains("picked up"), false);
        assertEquals(gameState.updateState("pick up the knife")
                .contains("picked up the knife"), true);
        assertEquals(gameState.updateState("pick up the knife")
                .contains("picked up the knife"), false);
    }

    @Test
    public void testRoom01CutSuite() {
        gameState.initOverworldState(new Room01());
        gameState.updateState("pick up the knife");
        assertEquals(gameState.updateState("cut the painting using the knife")
                .contains("cut the painting"), true);
        assertEquals(gameState.updateState("cut the painting using the knife")
                .contains("already"), true);
        testScratched("cut the table using the knife", true);
        testScratched("slash the door using the knife", true);
    }

    @Test
    public void testBreakSuite() {
        gameState.initOverworldState(new Room01());
        assertEquals(gameState.updateState("break the table").contains("broke the table"), true);
        assertEquals(gameState.updateState("break the table").contains("broke the table"), false);
        assertEquals(gameState.updateState("break the door")
                .contains("cannot break the door"), true);
    }

    @Test
    public void testOpenSuite() {
        gameState.initOverworldState(new Room01());
        assertEquals(gameState.updateState("open the table").contains("opened the drawer"), true);
        assertEquals(gameState.updateState("open the door").contains("locked"), true);
        gameState.updateState("pick up the knife");
        gameState.updateState("cut the painting");
        assertEquals(gameState.updateState("open the door")
                .contains("opened"), true);
    }

    @Test
    public void testUseSuite() {
        gameState.initOverworldState(new Room01());
        assertEquals(gameState.updateState("use your hand to pick up the knife")
                .contains("picked"), true);
        assertEquals(gameState.updateState("use something sharp")
                .contains("want to use the knife"), true);
        testScratched("use the knife to slash the door", true);
        testScratched("use the knife to cut the glass table", true);
        assertEquals(gameState.updateState("use the knife to cut the painting")
                .contains("cut the painting"), true);
        assertEquals(gameState.updateState("use the key to open the door")
                .contains("opened"), true);
    }

    @Test
    public void testShowActionsSuite() {
        gameState.initOverworldState(new Room01());
        assertEquals(gameState.updateState("show my commands")
                .contains("following actions"), true);
        assertEquals(gameState.updateState("take a look at my actions")
                .contains("following actions"), true);
    }

    @Test
    public void testMultipleCommandsSuite() {
        gameState.initOverworldState(new Room01());
        assertEquals(gameState
                .updateState("grab the knife and cut the painting then unlock the door")
                .contains("opened"), true);
    }
}
