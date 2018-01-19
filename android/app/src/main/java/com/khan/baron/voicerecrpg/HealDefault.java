package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 19/01/2018.
 */

public class HealDefault extends Action {
    public String run(GameState state) {
        getState(state);
        mHealth += Math.max(100, mHealth+100);
        return "You healed";
    }
}
