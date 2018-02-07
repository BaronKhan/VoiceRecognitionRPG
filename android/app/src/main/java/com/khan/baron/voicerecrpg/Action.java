package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    protected GameState.Mode mGameMode;
    protected Inventory mInventory;
    protected Enemy mCurrentEnemy = null;
    protected ItemActionMap mMap = null;
    protected Room mCurrentRoom = null;
    protected int mHealth = 0;
    protected String mActionContext = null;

    abstract String run(GameState state);

    void getState(GameState state) {
        //Get the child objects of state for easier access
        mGameMode = state.mGameMode;
        mInventory = state.mInventory;
        mCurrentEnemy = state.mCurrentEnemy;
        mMap = state.mMap;
        mCurrentRoom = state.mCurrentRoom;
        mHealth = state.mHealth;
        mActionContext = state.mActionContext;
    }
}
