package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.items.Weapon;

/**
 * Created by Baron on 14/01/2018.
 */

public class AttackDefault extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState)state);
            if (mGameMode == GameState.Mode.MODE_BATTLE) {
                if (mCurrentEnemy != null) {
                    if (mInventory.hasItem(Item.ItemType.ITEM_WEAPON)) {
                        Weapon chosenWeapon =
                                (Weapon)mInventory.getRandomItem(Item.ItemType.ITEM_WEAPON);
                        mCurrentEnemy.decreaseHealth(
                                (int)Math.floor(chosenWeapon.mDamageModifier*10));
                        state.actionSucceeded();
                        return "You attacked the " + mCurrentEnemy.getName()
                                + " with something (" + chosenWeapon.getName() + ").";
                    } else {
                        mCurrentEnemy.decreaseHealth(1);
                        state.actionSucceeded();
                        return "You don't have a weapon to attack with. You attacked the "
                                + mCurrentEnemy.getName() + "+ with your bare hands";
                    }
                } else {
                    state.actionFailed();
                    return "There is no " + mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        state.actionFailed();
        return "You can't do that right now.";
    }
}
