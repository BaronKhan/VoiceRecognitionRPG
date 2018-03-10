package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 19/01/2018.
 */

public class HealDefault extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState) state);
            mPlayerHealth += Math.max(100, mPlayerHealth + 100);
            return "You healed";
        }
        return "Error: unknown GlobalState object loaded.";
    }
}
