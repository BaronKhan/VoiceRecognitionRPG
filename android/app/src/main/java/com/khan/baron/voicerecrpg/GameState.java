package com.khan.baron.voicerecrpg;

import android.app.Activity;

import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room;
import com.khan.baron.voicerecrpg.rooms.Room01;

import java.io.IOException;
import java.net.URL;

import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_BATTLE;
import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_OVERWORLD;

public class GameState extends GlobalState {
    private Activity mMainActivity;

    // Environment Settings
    public enum Mode { MODE_OVERWORLD, MODE_BATTLE }

    public Inventory mInventory;

    private ContextActionMap mBattleMap;
    public VoiceProcess mBattleVoiceProcess;

    private ContextActionMap mOverworldMap;
    public VoiceProcess mOverworldVoiceProcess;

    protected Mode mGameMode;
    public Enemy mCurrentEnemy;
    public Room mCurrentRoom;

    public int mPlayerHealth = 100;

    protected String mInitStr;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;

        mBattleMap = new BattleContextActionMap(this);
        mBattleVoiceProcess = new VoiceProcess(mainActivity, this, mBattleMap);

        mOverworldMap = new OverworldContextActionMap(this);
        mOverworldVoiceProcess = new VoiceProcess(mainActivity, this, mOverworldMap);

        mInventory = new Inventory(mBattleMap, mOverworldMap);
    }

    public String getInitOutput() {
        return mInitStr;
    }

    public void addDictionary(URL url) throws IOException {
        mBattleVoiceProcess.addDictionary(url);
        mOverworldVoiceProcess.addDictionary(url);
    }

    public void initState() {
        mInventory.add(new Weapon("sword", "sharp", "long", "metallic"));
        mInventory.add(new Weapon("knife", "sharp", "short", "metallic"));
        mInventory.add(new Weapon("hammer", "heavy", "blunt"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("elixer"));
        mInventory.add(new Item("letter", Item.ItemType.ITEM_KEY, "paper", "document"));
//        initBattleState(new Troll(100));
        initOverworldState(new Room01());
    }

    public Context getBattleActionContext() { return mBattleVoiceProcess.getActionContext(); }

    public void setCurrentBattle(Enemy currentEnemy) {
        mCurrentRoom = null;
        mCurrentEnemy = currentEnemy;
        mBattleMap.addPossibleTarget(mCurrentEnemy);
        mBattleMap.setDefaultTarget(mCurrentEnemy);
    }

    public void setCurrentRoom(Room room) {
        mCurrentEnemy = null;
        mCurrentRoom = room;
        mOverworldMap.addPossibleTarget(mCurrentRoom);
        mOverworldMap.setDefaultTarget(mCurrentRoom);
    }

    public void initBattleState(Enemy currentEnemy) {
        mGameMode = MODE_BATTLE;
        setCurrentBattle(currentEnemy);
        mInitStr = "A "+currentEnemy.getName()+" appears in front of you!";
    }

    public void initOverworldState(Room room) {
        mGameMode = MODE_OVERWORLD;
        setCurrentRoom(room);
        mInitStr = room.getRoomDescription();
    }

    public Mode getGameMode() { return mGameMode; }

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
            overworldOutput += mOverworldVoiceProcess.processInput(input);
            return overworldOutput;
        }
    }
}
