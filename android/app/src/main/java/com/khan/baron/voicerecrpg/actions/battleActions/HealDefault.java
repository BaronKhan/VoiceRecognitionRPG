package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.items.Potion;

/**
 * Created by Baron on 19/01/2018.
 */

public class HealDefault extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getInventory().hasItem(Item.ItemType.ITEM_HEALING)) {
                Potion chosenPotion =
                        (Potion) gameState.getInventory().getRandomItem(Item.ItemType.ITEM_HEALING);
                gameState.getInventory().remove(chosenPotion.getName());
                gameState.setPlayerHealth(gameState.getPlayerHealth() + Math.max(100, gameState.getPlayerHealth() + 100));
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
