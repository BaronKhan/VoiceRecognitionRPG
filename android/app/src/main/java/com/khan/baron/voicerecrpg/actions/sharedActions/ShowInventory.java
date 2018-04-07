package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 12/03/2018.
 */

public class ShowInventory extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            state.actionFailed();   //By default, we don't want the enemy to have a turn
            if (currentTarget == gameState.mInventory) {
                String inventoryStr = "{";
                for (Context item : gameState.mInventory.mItems) {
                    inventoryStr+=item.getName()+", ";
                }
                inventoryStr+="}";
                return "This is your inventory:\n"+inventoryStr;
            } else {
                return "Intent not understood.";
            }
        }
        return "You can't do that right now";
    }
}
