package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 07/02/2018.
 */

public class DoNothing extends Action {
    public String run(GlobalState state, Context currentTarget) {
        return "You did nothing.";
    }
}
