package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 06/02/2018.
 */

public class HealItem extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getInventory().hasItem(gameState.getBattleActionContext().getName())) {
                gameState.incPlayerHealth(100);
                //Remove healing item from inventory
                gameState.getInventory().remove(gameState.getBattleActionContext().getName());
                state.actionSucceeded();
                return "You healed with a " + gameState.getBattleActionContext().getName();
            } else {
                state.actionFailed();
                return "You don't have a " + gameState.getBattleActionContext().getName();
            }
        }
        state.actionSucceeded();
        return "Error: unknown GlobalState loaded.";
    }
}
