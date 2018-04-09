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
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE) {
                if (gameState.getCurrentEnemy() != null) {
                    if (gameState.getInventory().hasItem(Item.ItemType.ITEM_WEAPON)) {
                        Weapon chosenWeapon =
                                (Weapon) gameState.getInventory()
                                        .getRandomItem(Item.ItemType.ITEM_WEAPON);
                        gameState.getCurrentEnemy().decreaseHealth(
                                (int)Math.floor(chosenWeapon.mDamageModifier*10));
                        state.actionSucceeded();
                        return "You attacked the " + gameState.getCurrentEnemy().getName()
                                + " with something (" + chosenWeapon.getName() + ").";
                    } else {
                        gameState.getCurrentEnemy().decreaseHealth(1);
                        state.actionSucceeded();
                        return "You don't have a weapon to attack with. You attacked the "
                                + gameState.getCurrentEnemy().getName() + "+ with your bare hands";
                    }
                } else {
                    state.actionFailed();
                    return "There is no " + gameState.getCurrentEnemy().getName() + " to attack.";
                }
            }
        }
        state.actionFailed();
        return "You can't do that right now.";
    }
}
