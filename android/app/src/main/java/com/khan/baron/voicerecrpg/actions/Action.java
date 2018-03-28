package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.rooms.Room;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    protected GameState.Mode mGameMode;
    protected Inventory mInventory;
    protected Enemy mCurrentEnemy = null;
    protected ContextActionMap mMap = null;
    protected Room mCurrentRoom = null;
    protected int mPlayerHealth = 0;
    protected Context mActionContext = null;

    public abstract Object run(GlobalState state, Context currentTarget);

    public void getState(GameState state) {
        //Get the child objects of state for easier access
        mGameMode = state.mGameMode;
        mInventory = state.mInventory;
        mCurrentEnemy = state.mCurrentEnemy;
        mMap = state.mBattleMap;
        mCurrentRoom = state.mCurrentRoom;
        mPlayerHealth = state.mPlayerHealth;
        mActionContext = state.mBattleVoiceProcess.getActionContext();
    }
}
