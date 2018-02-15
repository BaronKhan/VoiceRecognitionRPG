package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 07/02/2018.
 */

public class DoNothing extends Action {
    public String run(GameState state) {
        return "You did nothing.";
    }
}
