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
            String itemName = Action.getCurrentContext().getName();
            if (gameState.getInventory().hasItem(itemName)) {
                gameState.incPlayerHealth(100);
                //Remove healing item from inventory
                gameState.getInventory().remove(itemName);
                state.actionSucceeded();
                return "You healed with a " + itemName;
            } else {
                state.actionFailed();
                return "You don't have a " + itemName;
            }
        }
        state.actionFailed();
        return "Error: unknown GlobalState loaded.";
    }
}
