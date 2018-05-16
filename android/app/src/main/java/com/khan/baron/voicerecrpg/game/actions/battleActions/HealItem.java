package com.khan.baron.voicerecrpg.game.actions.battleActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;

/**
 * Created by Baron on 06/02/2018.
 */

public class HealItem extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getInventory().hasItem(Action.getCurrentContext().getName())) {
                gameState.incPlayerHealth(100);
                //Remove healing item from inventory
                gameState.getInventory().remove(Action.getCurrentContext().getName());
                state.actionSucceeded();
                return "You healed with a " + Action.getCurrentContext().getName();
            } else {
                state.actionFailed();
                return "You don't have a " + Action.getCurrentContext().getName();
            }
        }
        state.actionSucceeded();
        return "Error: unknown GlobalState loaded.";
    }
}
