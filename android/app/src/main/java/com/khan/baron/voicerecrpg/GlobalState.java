package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 09/03/2018.
 */

//Derived classes should contain the environment objects to be used in actions
public abstract class GlobalState {
    protected boolean mActionSucceeded = true;

    public void actionSucceeded() { mActionSucceeded = true; }

    public void actionFailed() {
        mActionSucceeded = true;
    }

    public boolean getActionSucceed() { return mActionSucceeded; }
}
