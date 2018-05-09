package com.khan.baron.voicerecrpg.game.actions.sharedActions;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.Inventory;
import com.khan.baron.voicerecrpg.system.Action;

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
