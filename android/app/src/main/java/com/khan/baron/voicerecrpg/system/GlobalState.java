package com.khan.baron.voicerecrpg.system;

/**
 * Created by Baron on 09/03/2018.
 */

//Derived classes should contain the environment objects to be used in actions
public abstract class GlobalState {
    protected boolean mActionSucceeded = true;

    public void actionSucceeded() { mActionSucceeded = true; }

    public void actionFailed() {
        mActionSucceeded = false;
    }

    public boolean getActionSucceeded() { return mActionSucceeded; }
}
