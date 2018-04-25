package com.khan.baron.voicerecrpg;

import android.app.Activity;

import com.khan.baron.voicerecrpg.actions.sharedActions.ShowActions;
import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.rooms.Room;
import com.khan.baron.voicerecrpg.rooms.Room01;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;

import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_BATTLE;
import static com.khan.baron.voicerecrpg.GameState.Mode.MODE_OVERWORLD;

public class GameState extends GlobalState {
    private Activity mMainActivity;

    // Environment Settings
    public enum Mode { MODE_OVERWORLD, MODE_BATTLE }

    protected Inventory mInventory;

    private ContextActionMap mBattleMap;
    protected MultipleCommandProcess mBattleVoiceProcess;

    private ContextActionMap mOverworldMap;
    protected MultipleCommandProcess mOverworldVoiceProcess;

    protected Mode mGameMode;
    protected Enemy mCurrentEnemy;
    protected Room mCurrentRoom;

    protected int mPlayerHealth = 100;

    protected String mInitOutput;

    private static boolean sStartOverworld = true;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
        mGameMode = MODE_OVERWORLD;
        mCurrentEnemy = null;

        mBattleMap = new BattleContextActionMap(this);
        mBattleVoiceProcess = new MultipleCommandProcess(mainActivity, this, mBattleMap);

        mOverworldMap = new OverworldContextActionMap(this);
        mOverworldVoiceProcess = new MultipleCommandProcess(mainActivity, this, mOverworldMap);

        mInventory = new Inventory(mBattleMap, mOverworldMap);
    }

    public void addDictionary(URL url) throws IOException {
        mBattleVoiceProcess.addDictionary(url);
        mOverworldVoiceProcess.addDictionary(url);
    }

    public void initState() {
        mInventory.add(new Weapon("sword", "sharp", "long", "metallic"));
        mInventory.add(new Weapon("hammer", "heavy", "blunt"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("potion"));
        mInventory.add(new Potion("elixer"));
        if (sStartOverworld) { initOverworldState(new Room01()); }
        else { initBattleState(new Troll(100)); }
    }

    public Entity getBattleActionContext() { return mBattleVoiceProcess.getActionContext(); }

    public Entity getOverworldActionContext() { return mOverworldVoiceProcess.getActionContext(); }

    public Entity getContext() {
        return (mGameMode == Mode.MODE_BATTLE)
            ? getBattleActionContext()
            : getOverworldActionContext();
    }

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
        mOverworldMap.addPossibleTargets(mCurrentRoom.getRoomObjects());
    }

    public void initBattleState(Enemy currentEnemy) {
        mGameMode = MODE_BATTLE;
        setCurrentBattle(currentEnemy);
        mInitOutput = "A "+currentEnemy.getName()+" appears in front of you!\n\n"
                +new ShowActions().execute(this, mBattleMap);
    }

    public void initOverworldState(Room room) {
        mGameMode = MODE_OVERWORLD;
        setCurrentRoom(room);
        mInitOutput = room.getRoomDescription()+"\n\n"
                +new ShowActions().execute(this, mOverworldMap);
    }

    public Mode getGameMode() { return mGameMode; }

    public String updateState(String input) {
        if (mGameMode == MODE_BATTLE) {
            Queue<String> commandQueue = mBattleVoiceProcess.splitInput(input);
            String totalOutput = "";
            while (!commandQueue.isEmpty()) {
                String actionOutput = "";
                String enemyOutput = "";
                String playerOutput = "";
                actionOutput += mBattleVoiceProcess.executeCommand(commandQueue);
                boolean acceptedAction = getActionSucceeded();
                if (acceptedAction) { enemyOutput += mCurrentEnemy.takeTurn(this) + "\n"; }
                enemyOutput += mCurrentEnemy.getName() + "'s health: " + mCurrentEnemy.getHealth() +
                        " / " + mCurrentEnemy.getMaxHealth();
                playerOutput += "Your health: " + mPlayerHealth + " / " + 100;
                totalOutput += actionOutput + "\n\n" + enemyOutput + " | " + playerOutput
                        + ((commandQueue.isEmpty()) ? "" : "\n\n---\n\n");
            }
            return totalOutput;
        } else {    //mGameMode == MODE_OVERWORLD
            String overworldOutput = "";
            Queue<String> commandQueue = mBattleVoiceProcess.splitInput(input);
            while (!commandQueue.isEmpty()) {
                overworldOutput += mOverworldVoiceProcess.executeCommand(commandQueue)
                        + ((commandQueue.isEmpty()) ? "" : "\n\n---\n\n");
            }
            return overworldOutput;
        }
    }

    public Inventory getInventory() { return mInventory; }

    public void setInventory(Inventory mInventory) { this.mInventory = mInventory; }

    public MultipleCommandProcess getBattleVoiceProcess() { return mBattleVoiceProcess; }

    public void setBattleVoiceProcess(MultipleCommandProcess mBattleVoiceProcess) {
        this.mBattleVoiceProcess = mBattleVoiceProcess;
    }

    public MultipleCommandProcess getOverworldVoiceProcess() { return mOverworldVoiceProcess; }

    public void setOverworldVoiceProcess(MultipleCommandProcess mOverworldVoiceProcess) {
        this.mOverworldVoiceProcess = mOverworldVoiceProcess;
    }

    public Enemy getCurrentEnemy() { return mCurrentEnemy; }

    public void setCurrentEnemy(Enemy mCurrentEnemy) { this.mCurrentEnemy = mCurrentEnemy; }

    public Room getCurrentRoom() { return mCurrentRoom; }

    public int getPlayerHealth() { return mPlayerHealth; }

    public void setPlayerHealth(int mPlayerHealth) { this.mPlayerHealth = mPlayerHealth; }

    public void decPlayerHealth(int dec) {
        this.mPlayerHealth = Math.max(this.mPlayerHealth-dec, 0);
    }

    public void incPlayerHealth(int inc) {
        this.mPlayerHealth = Math.min(this.mPlayerHealth+inc, 100);
    }

    public String getInitOutput() { return mInitOutput; }

    public void setInitOutput(String mInitStr) { this.mInitOutput = mInitStr; }

    public static boolean getStartOverworld() {
        return sStartOverworld;
    }

    public static void setStartOverworld(boolean sStartOverworld) {
        GameState.sStartOverworld = sStartOverworld;
    }
}
