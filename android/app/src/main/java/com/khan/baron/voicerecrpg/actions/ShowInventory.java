package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 12/03/2018.
 */

public class ShowInventory extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState)state);
            if (currentTarget == mInventory) {
                String inventoryStr = "{";
                for (Context item : mInventory.mItems) {
                    inventoryStr+=item.getName()+",";
                }
                inventoryStr+="}";
                return "This is your inventory:\n"+inventoryStr;
            } else {
//                return "Intent not understood (current target = "+currentTarget.getName()+")";
                return "Intent not understood";
            }
        }
        return "You can't do that right now";
    }
}
