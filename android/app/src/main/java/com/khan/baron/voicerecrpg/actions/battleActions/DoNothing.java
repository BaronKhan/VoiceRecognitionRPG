package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 07/02/2018.
 */

public class DoNothing extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();
        return "You did nothing.";
    }
}
