package com.khan.baron.voicerecrpg.system;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    public abstract Object execute(GlobalState state, Entity currentTarget);
}
