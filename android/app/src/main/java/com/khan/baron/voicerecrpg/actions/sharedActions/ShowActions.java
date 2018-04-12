package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

public class ShowActions extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();   //By default, we don't want the enemy to have a turn
        if (currentTarget instanceof ContextActionMap) {
            String actionsStr = "{";
            for (String action : ((ContextActionMap) currentTarget).getActions()) {
                actionsStr+=action+", ";
            }
            actionsStr+="}";
            return "The following actions are available e.g. \"look around\", \"show my inventory" +
                    "\":\n"+actionsStr;
        } else {
            return "Intent not understood.";
        }
    }
}
