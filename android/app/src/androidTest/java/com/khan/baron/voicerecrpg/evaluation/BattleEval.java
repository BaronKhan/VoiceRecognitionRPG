package com.khan.baron.voicerecrpg.evaluation;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Potion;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.system.ContextActionMap;

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
public class BattleEval {
    private GameState gameState;

    public BattleEval() {
        gameState = new GameState(null);
        ContextActionMap.setRememberUserSynonyms(false);
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

    private void testAttackedTroll(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("attacked the troll"), correctInput);
    }

    private void testAttackedWithHammer(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("hammer"), correctInput);
    }

    private void testAttackedWithSword(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("sword"), correctInput);
    }

    @Test
    public void testAttackInputSuite() {
        gameState.initBattleState(new Troll(9999999));
        testAttackedTroll("attack", true);
        EvaluateMechanisms.runTest(gameState.updateState("attack").contains("hands"), true);
        testAttackedTroll("attack with everything you have got", true);
        testAttackedTroll("use", false);
        testAttackedTroll("hit", true);
        testAttackedTroll("hit the troll", true);
        testAttackedTroll("punch the troll", true);
        EvaluateMechanisms.runTest(gameState.updateState("heal").contains("attacked"), false);
        testAttackedTroll("heal", false);
        testAttackedTroll("launch an assault", true);
        testAttackedTroll("charge at the troll", true);
        testAttackedTroll("jump up and down", false);
        testAttackedTroll("fight the troll", true);
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        testAttackedTroll("attack the troll", true);
        EvaluateMechanisms.runTest(gameState.updateState("hit the troll")
                .contains("You attacked the troll"), true);
        testAttackedTroll("attack", true);
        testAttackedWithHammer("attack with the hammer", true);
        testAttackedWithHammer("attack with the sledgehammer", true);
        testAttackedWithHammer("attack the troll with the sledgehammer", true);
        testAttackedTroll("use something to attack with", true);
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal"));
        testAttackedWithSword("use a sword attack", true);
    }

    @Test
    public void testCorrectContexts() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword"));
        gameState.getInventory().add(new Weapon("hammer"));
        testAttackedWithSword("attack the troll with a sword", true);
        testAttackedWithHammer("attack the troll with a hammer", true);
        testAttackedWithSword("attack the troll with a sword", true);
        testAttackedWithHammer("attack the troll with a hammer", true);
    }

    private void testHealed(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("healed"), correctInput);
    }

    private void testInventoryShown(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("your inventory"), correctInput);
    }

    private void testLookAround(String input, boolean correctInput) {
        EvaluateMechanisms.runTest(gameState.updateState(input).contains("in battle"), correctInput);
    }

    @Test
    public void testHealInputSuite() {
        gameState.initBattleState(new Troll(9999999));
        for (int i = 0; i < 100; ++i) { gameState.getInventory().add(new Potion("potion")); }
        for (int i = 0; i < 100; ++i) { gameState.getInventory().add(new Potion("elixir")); }
        gameState.getInventory().add(new Weapon("sword"));
        testHealed("heal", true);
        testHealed("use a potion to heal", true);
        testHealed("hit", false);
        testHealed("heal with elixir", true);
        testHealed("use an elixir right now before it is too late to heal", true);
        testHealed("recover with an elixir", true);
        testHealed("Use something to heal with", true);
        testHealed("heal myself with a potion", true);
    }

    @Test
    public void testSentenceStructure() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword"));
        gameState.getInventory().add(new Weapon("hammer"));
        testAttackedWithSword("attack the troll with a sword", true);
    }

    @Test
    public void testDescriptions() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal", "pointy"));
        testAttackedWithHammer("attack with something blunt", true);
        testAttackedWithSword("hit the troll with something pointy please", true);
        testAttackedWithSword("launch an assault towards the troll using something sharp", true);
        testAttackedWithHammer("use a heavy attack", true);
    }

    @Test
    public void testInventorySuite() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal", "pointy"));
        testInventoryShown("show my inventory", true);
        testInventoryShown("show my troll", false);
        testInventoryShown("show my bag", true);
        testInventoryShown("please show the contents of my possessions that I have", true);
        testInventoryShown("look at my inventory", true);
    }

    @Test
    public void testUseSuite() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword", "sharp", "metal", "pointy"));
        gameState.getInventory().add(new Potion("potion"));
        gameState.getInventory().add(new Potion("potion"));
        testAttackedWithSword("use the sword to attack", true);
        testHealed("use a potion to attack", false);
        testAttackedTroll("use something to attack with", true);
        EvaluateMechanisms.runTest(gameState.updateState("use a potion")
                .contains("want to use the potion"), true);
        EvaluateMechanisms.runTest(gameState.updateState("use something sharp")
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
        EvaluateMechanisms.runTest(gameState.updateState("show my commands").contains("following actions"), true);
        EvaluateMechanisms.runTest(gameState.updateState("look at my actions")
                .contains("following actions"), true);
    }

    @Test
    public void testLearningSuite() {
        gameState.initBattleState(new Troll(9999999));
        EvaluateMechanisms.runTest(gameState.updateState("strike means attack").contains("synonym"), true);
        testAttackedTroll("strike the troll", true);
    }

    @Test
    public void testConfirmation() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword", "sharp", "long", "metallic"));
        for (int i = 0; i < 100; ++i) { gameState.getInventory().add(new Potion("potion")); }
        EvaluateMechanisms.runTest(gameState.updateState("obliterate the troll with the sword")
                .contains("you mean, \"attack"), true);
        testAttackedWithSword("yeah", true);
        EvaluateMechanisms.runTest(gameState.updateState("kick the troll").contains("you mean, \"attack"), true);
        testAttackedWithSword("yeah", true);
        EvaluateMechanisms.runTest(gameState.updateState("kick").contains("you mean, \"attack"), true);
        testAttackedWithSword("yeah", true);
        // kick added as synonym
        testAttackedWithSword("kick the troll", true);
        EvaluateMechanisms.runTest(gameState.updateState("regenerate using a potion")
                .contains("you mean, \"heal"), true);
        testHealed("yes", true);
        EvaluateMechanisms.runTest(gameState.updateState("use something to regenerate with")
                .contains("you mean"), true);
        testHealed("yes", true);
        //regenerate added as synonym
        testHealed("regenerate", true);
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        gameState.updateState("attack with a bang");
        testAttackedWithHammer("yes", true);
    }

    @Test
    public void testMultipleCommandsSuite() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword", "sharp", "long", "metallic"));
        gameState.getInventory().add(new Weapon("hammer", "blunt", "heavy"));
        for (int i = 0; i < 100; ++i) { gameState.getInventory().add(new Potion("potion")); }
        EvaluateMechanisms.runTest(gameState.updateState("obliterate the troll with the sword and then " +
                "regenerate using a potion").contains("you mean, \"attack"), true);
        testAttackedWithSword("yes", true);
        testHealed("yeah", true);
        testAttackedWithHammer("attack with a sword and hammer", true);
        testAttackedWithSword("attack with a hammer and with a sword", true);
    }

    @Test
    public void testSentenceMatching() {
        gameState.initBattleState(new Troll(9999999));
        gameState.getInventory().add(new Weapon("sword", "sharp", "long", "metallic"));
        EvaluateMechanisms.runTest(gameState.updateState("what are my actions")
                .contains("following actions"), true);
        EvaluateMechanisms.runTest(gameState.updateState("what actions can i do")
                .contains("following actions"), true);
        testInventoryShown("what is in my bag", true);
        testInventoryShown("hello world", false);
    }
}
