package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

import edu.mit.jwi.Dictionary;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class GameStateTest {
    public GameState gameState;

    public GameStateTest() {
        gameState = new GameState(null);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                URL url = new URL("file", null, dictFile.getPath());
                gameState.mDict = new Dictionary(url);
                gameState.mDict.open();
                gameState.mDb = new CustomWordNet(gameState.mDict);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.khan.baron.voicerecrpg", appContext.getPackageName());
    }

    @Test
    public void testAttackInput() {
        gameState.initBattleState(new Troll(9999999));
        assertEquals(gameState.updateState("attack").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("attack with everything you've got").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("use").contains("attacked"), false);
        assertEquals(gameState.updateState("hit").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("heal").contains("attacked"), false);
        assertEquals(gameState.updateState("launch an assault").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("charge at the troll").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("jump up and down").contains("You attacked the troll."), false);
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        assertEquals(gameState.updateState("attack with the hammer").contains("hammer"), true);
        assertEquals(gameState.updateState("attack with the sledgehammer").contains("hammer"), true);
        assertEquals(gameState.updateState("attack the troll").contains("You attacked the troll."), true);
        assertEquals(gameState.updateState("hit the troll").contains("You attacked the troll."), true);
    }

    @Test
    public void testHealInput() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("elixer"));
        gameState.mInventory.add(new Weapon("sword"));
        assertEquals(gameState.updateState("heal").contains("healed"), true);
        assertEquals(gameState.updateState("use a potion").contains("healed"), true);
        assertEquals(gameState.updateState("hit").contains("healed"), false);
        assertEquals(gameState.updateState("heal with elixer").contains("healed"), true);
    }
}
