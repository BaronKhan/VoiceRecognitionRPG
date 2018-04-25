package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

public class ShowActions extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();   //By default, we don't want the enemy to have a turn
        if (currentTarget instanceof ContextActionMap) {
            StringBuilder actionsStr = new StringBuilder("{ | ");
            for (String action : ((ContextActionMap) currentTarget).getActions()) {
                actionsStr.append(action).append(" | ");
            }
            actionsStr.append("}");
            return "The following actions are available:\n" +actionsStr
                    +"\n\nGive commands such as \"look around\", \"show my inventory\", etc.";
        } else {
            return "Intent not understood.";
        }
    }
}
