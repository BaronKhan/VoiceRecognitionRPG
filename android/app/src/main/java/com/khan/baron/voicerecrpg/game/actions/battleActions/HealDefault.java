package com.khan.baron.voicerecrpg.game.actions.battleActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.items.Potion;

/**
 * Created by Baron on 19/01/2018.
 */

public class HealDefault extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getInventory().hasItem(Item.ItemType.ITEM_HEALING)) {
                Potion chosenPotion =
                        (Potion) gameState.getInventory().getRandomItem(Item.ItemType.ITEM_HEALING);
                gameState.getInventory().remove(chosenPotion.getName());
                gameState.incPlayerHealth(100);
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
