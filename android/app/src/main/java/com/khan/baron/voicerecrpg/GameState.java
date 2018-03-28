package com.khan.baron.voicerecrpg;

import android.app.Activity;

import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import edu.mit.jwi.Dictionary;

import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_BATTLE;
import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_OVERWORLD;

public class GameState extends GlobalState {
    protected Activity mMainActivity;

    // Environment Settings
    public enum Mode { MODE_OVERWORLD, MODE_BATTLE }

    public Inventory mInventory;
    public ContextActionMap mBattleMap;
    public VoiceProcess mBattleVoiceProcess;

    public Mode mGameMode;
    public Enemy mCurrentEnemy;
    public Room mCurrentRoom;

    public int mPlayerHealth = 100;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;
        mBattleMap = new BattleContextActionMap(this);
        mInventory = new Inventory(mBattleMap);
        mBattleVoiceProcess = new VoiceProcess(mainActivity, this, mBattleMap);
    }

    public void addDictionary(URL url) throws IOException {
        mBattleVoiceProcess.addDictionary(url);
    }

    public void initState() {
        initBattleState(new Troll(100));
        mInventory.add(new Weapon("sword", "sharp", "long", "metallic"));
        mInventory.add(new Weapon("knife", "sharp", "short", "metallic"));
        mInventory.add(new Weapon("hammer", "heavy", "blunt"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("elixer"));
    }

    public void setCurrentEnemy(Enemy currentEnemy) {
        mCurrentEnemy = currentEnemy;
        mBattleMap.setPossibleTargets(new ArrayList<Context>(Arrays.asList(currentEnemy, mInventory)));
        mBattleMap.setDefaultTarget(mCurrentEnemy);
    }

    public void initBattleState(Enemy currentEnemy) {
        mGameMode = MODE_BATTLE;
        setCurrentEnemy(currentEnemy);
    }

    public void initOverworldState(Room room) {
        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;
        mCurrentRoom = room;
    }

    public String updateState(String input) {
        String actionOutput = "";

        if (mGameMode == MODE_BATTLE) {
            String enemyOutput = "";
            String playerOutput = "";

            actionOutput += mBattleVoiceProcess.processInput(input);
            boolean acceptedAction = getActionSucceeded();
            if (acceptedAction) { enemyOutput += mCurrentEnemy.takeTurn(this) + "\n"; }

            enemyOutput += mCurrentEnemy.getName() + "'s health: " + mCurrentEnemy.getHealth() +
                    " / " + mCurrentEnemy.getMaxHealth();
            playerOutput += "Your health: " + mPlayerHealth + " / " + 100;

            return actionOutput + "\n\n" + enemyOutput + " | " + playerOutput;
        } else {    //mGameMode == MODE_OVERWORLD
            String overworldOutput = "";
        }

        return "None";
    }
}
