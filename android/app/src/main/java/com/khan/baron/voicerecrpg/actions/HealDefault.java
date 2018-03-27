package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.items.Potion;
import com.khan.baron.voicerecrpg.items.Weapon;

/**
 * Created by Baron on 19/01/2018.
 */

public class HealDefault extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState) state);
            if (mInventory.hasItem(Item.ItemType.ITEM_HEALING)) {
                Potion chosenPotion =
                        (Potion)mInventory.getRandomItem(Item.ItemType.ITEM_HEALING);
                mInventory.remove(chosenPotion.getName());
                mPlayerHealth += Math.max(100, mPlayerHealth + 100);
                state.actionSucceeded();
                return "You healed with a " + chosenPotion.getName() + ".";
            } else {
                state.actionFailed();
                return "You have nothing to heal with.";
            }
        }
        state.actionFailed();
        return "Error: unknown GlobalState object loaded.";
    }
}
