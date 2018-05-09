package com.khan.baron.voicerecrpg.game.actions.battleActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;

/**
 * Created by Baron on 07/02/2018.
 */

public class DoNothing extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();
        return "You did nothing.";
    }
}
