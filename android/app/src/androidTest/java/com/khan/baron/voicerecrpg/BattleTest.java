package com.khan.baron.voicerecrpg;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;

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
public class BattleTest {
    private GameState gameState;

    public BattleTest() {
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

    void testAttackedTroll(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("attacked the troll"), correctInput);
    }

    void testAttackedWithHammer(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("hammer"), correctInput);
    }

    void testAttackedWithSword(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("sword"), correctInput);
    }

    @Test
    public void testAttackInputSuite() {
        gameState.initBattleState(new Troll(9999999));
        testAttackedTroll("attack", true);
        assertEquals(gameState.updateState("attack").contains("hands"), true);
        testAttackedTroll("attack with everything you have got", true);
        testAttackedTroll("use", false);
        testAttackedTroll("hit", true);
        testAttackedTroll("hit the troll", true);
        testAttackedTroll("punch the troll", true);
        testAttackedTroll("kick the troll", true);
        assertEquals(gameState.updateState("heal").contains("attacked"), false);
        testAttackedTroll("heal", false);
        testAttackedTroll("launch an assault", true);
        testAttackedTroll("charge at the troll", true);
        //TODO: jump detected, use cosine similarity to ignore
//        testAttackedTroll("jump up and down", false);
        testAttackedTroll("fight the troll", true);
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        testAttackedTroll("attack the troll", true);
        assertEquals(gameState.updateState("hit the troll")
                .contains("You attacked the troll"), true);
        testAttackedTroll("attack", true);
        testAttackedWithHammer("attack with the hammer", true);
        testAttackedWithHammer("attack with the sledgehammer", true);
        testAttackedWithHammer("attack the troll with the sledgehammer", true);
        testAttackedTroll("use something to attack with", true);
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal"));
        testAttackedWithSword("use a sword attack", true);
    }

    void testHealed(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("healed"), correctInput);
    }

    void testInventoryShown(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input).contains("your inventory"), correctInput);
    }

    private void testLookAround(String input, boolean correctInput) {
        assertEquals(gameState.updateState(input)
                .contains("in battle"), correctInput);
    }

    @Test
    public void testHealInputSuite() {
        gameState.initBattleState(new Troll(9999999));
        for (int i = 0; i < 100; ++i) { gameState.mInventory.add(new Potion("potion")); }
        for (int i = 0; i < 100; ++i) { gameState.mInventory.add(new Potion("elixer")); }
        gameState.mInventory.add(new Weapon("sword"));
        testHealed("heal", true);
        testHealed("use a potion to heal", true);
        testHealed("hit", false);
        testHealed("heal with elixer", true);
        testHealed("use an elixer right now before it is too late to heal", true);
        testHealed("recover with an elixer", true);
        testHealed("Use something to heal with", true);
        testHealed("Use something to regenerate with", true);
    }

    @Test
    public void testSentenceStructure() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Weapon("sword"));
        testAttackedWithSword("attack the troll with a sword", true);
    }

    @Test
    public void testDescriptions() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        testAttackedWithHammer("attack with something blunt", true);
        testAttackedWithSword("hit the troll with something pointy please", true);
        testAttackedWithSword("launch an assault towards the troll using something sharp", true);
        testAttackedWithHammer("use a heavy attack", true);
    }

    @Test
    public void testInventorySuite() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Weapon("hammer", "blunt", "heavy"));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("show my inventory", true);
        testInventoryShown("show my troll", false);
        testInventoryShown("show my bag", true);
        testInventoryShown("please show the contents of my possessions that I have", true);
        testInventoryShown("look at my inventory", true);
    }

    @Test
    public void testUseSuite() {
        gameState.initBattleState(new Troll(9999999));
        gameState.mInventory.add(new Weapon("sword", "sharp", "metal", "pointy"));
        gameState.mInventory.add(new Potion("potion"));
        gameState.mInventory.add(new Potion("potion"));
        testAttackedWithSword("use the sword to attack", true);
        testHealed("use a potion to attack", false);
        testAttackedTroll("use something to attack with", true);
        assertEquals(gameState.updateState("use a potion")
                .contains("want to use the potion"), true);
        assertEquals(gameState.updateState("use something sharp")
                .contains("want to use the sword"), true);
    }

    @Test
    public void testLookAroundSuite() {
        gameState.initBattleState(new Troll(9999999));
        testLookAround("look around", true);
        testLookAround("look at the troll", true);
    }

    @Test
    public void testShowActionsSuite() {
        gameState.initBattleState(new Troll(9999999));
        assertEquals(gameState.updateState("show my commands")
                .contains("following actions"), true);
        assertEquals(gameState.updateState("look at my actions")
                .contains("following actions"), true);
    }

    @Test
    public void testLearningSuite() {
        gameState.initBattleState(new Troll(9999999));
        assertEquals(gameState.updateState("strike means attack")
                .contains("synonym"), true);
        testAttackedTroll("strike the troll", true);
    }
}