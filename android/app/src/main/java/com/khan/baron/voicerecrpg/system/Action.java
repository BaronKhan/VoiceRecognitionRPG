package com.khan.baron.voicerecrpg.system;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    public abstract Object execute(GlobalState state, Entity currentTarget);

    public Object processReply(GlobalState state, String input) {
        return "Intent not understood.";
    }

    protected boolean mWantsReply = false;
    protected Entity mCurrentTarget;

    public boolean wantsReply() {
        return mWantsReply;
    }

    public void setWantsReply(boolean mWantsReply) {
        this.mWantsReply = mWantsReply;
    }
}
