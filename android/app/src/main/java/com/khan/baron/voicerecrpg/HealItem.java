package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 06/02/2018.
 */

public class HealItem extends Action {
    public String run(GameState state) {
        getState(state);
        if (mInventory.hasItem(mActionContext.getName())) {
            mHealth += Math.max(100, mHealth + 100);
            //Remove healing item from inventory
            mInventory.remove(mActionContext.getName());
            return "You healed with " + mActionContext.getName();
        } else {
            return "You don't have a " +mActionContext.getName();
        }
    }
}
