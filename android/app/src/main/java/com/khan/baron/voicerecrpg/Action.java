package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 11/01/2018.
 */

public class Action {
    Runnable mRun, mDefaultRun;

    public Action() {
        mRun = null;
        mDefaultRun = null;
    }

    public void defaultMethod() {
        if (mDefaultRun != null) {
            mDefaultRun.run();
        }
    }

    public void myMethod() {
        if (mRun != null) {
            mRun.run();
        }
    }

    public void setDefaultMethod(Runnable defaultRun) {
        mDefaultRun = defaultRun;
    }

    public void setMethod(Runnable toRun) {
        mDefaultRun = mRun;
    }

}
