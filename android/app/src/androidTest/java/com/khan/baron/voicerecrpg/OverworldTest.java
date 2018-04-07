package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room01;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class OverworldTest {
    public GameState gameState;

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

    @Test
    public void testInventorySuite() {
        gameState.initOverworldState(new Room01());
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("show my inventory", true);
        testInventoryShown("show my troll", false);
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
}
