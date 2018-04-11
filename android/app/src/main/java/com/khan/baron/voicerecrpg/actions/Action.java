package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    public abstract Object execute(GlobalState state, Entity currentTarget);
}
