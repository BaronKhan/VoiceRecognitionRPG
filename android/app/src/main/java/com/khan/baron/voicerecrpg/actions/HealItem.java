package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 06/02/2018.
 */

public class HealItem extends Action {
    public String run(GameState state) {
        getState(state);
        if (mInventory.hasItem(mActionContext)) {
            mPlayerHealth += Math.max(100, mPlayerHealth + 100);
            //Remove healing item from inventory
            mInventory.remove(mActionContext);
            return "You healed with " + mActionContext;
        } else {
            return "You don't have a " +mActionContext;
        }
    }
}
