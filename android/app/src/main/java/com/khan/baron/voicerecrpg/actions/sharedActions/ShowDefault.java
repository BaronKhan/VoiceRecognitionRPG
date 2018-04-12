package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.actions.Action;

public class ShowDefault extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            state.actionFailed();   //By default, we don't want the enemy to have a turn
            if (currentTarget instanceof Inventory) {
                return new ShowInventory().execute(state, currentTarget);
            } else if (currentTarget instanceof ContextActionMap) {
                return new ShowActions().execute(state, currentTarget);
            } else {
                return "Intent not understood.";
            }
        }
        return "You can't do that right now";
    }
}
