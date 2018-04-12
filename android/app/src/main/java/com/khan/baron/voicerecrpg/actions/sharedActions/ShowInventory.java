package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 12/03/2018.
 */

public class ShowInventory extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            state.actionFailed();   //By default, we don't want the enemy to have a turn
            if (currentTarget instanceof Inventory) {
                String inventoryStr = "{ | ";
                for (Entity item : gameState.getInventory().mItems) {
                    inventoryStr+=item.getName()+" | ";
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
