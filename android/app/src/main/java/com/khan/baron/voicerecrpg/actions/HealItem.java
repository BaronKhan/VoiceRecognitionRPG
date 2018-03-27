package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 06/02/2018.
 */

public class HealItem extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState) state);
            if (mInventory.hasItem(mActionContext.getName())) {
                mPlayerHealth += Math.max(100, mPlayerHealth + 100);
                //Remove healing item from inventory
                mInventory.remove(mActionContext.getName());
                state.actionSucceeded();
                return "You healed with " + mActionContext;
            } else {
                state.actionFailed();
                return "You don't have a " + mActionContext;
            }
        }
        state.actionSucceeded();
        return "Error: unknown GlobalState loaded.";
    }
}
