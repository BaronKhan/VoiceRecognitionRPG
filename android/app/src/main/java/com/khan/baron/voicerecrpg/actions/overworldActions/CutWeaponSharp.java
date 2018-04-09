package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.objects.general.Cuttable;
import com.khan.baron.voicerecrpg.objects.room01.Painting;
import com.khan.baron.voicerecrpg.rooms.Room01;

public class CutWeaponSharp extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            Context currentContext = gameState.getOverworldActionContext();
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())
                    && currentTarget instanceof Cuttable) {
                state.actionSucceeded();
                if (gameState.getCurrentRoom() instanceof Room01
                        && currentTarget instanceof Painting) {
                    if (gameState.getCurrentRoom().getRoomState() != Room01.StateRoom01.PAINTING_CUT.ordinal()) {
                        gameState.getInventory().add(new Item("key", Item.ItemType.ITEM_KEY, "access"));
                        gameState.getCurrentRoom().setRoomState(
                                Room01.StateRoom01.PAINTING_CUT.ordinal());
                        return "You cut the painting of the tree down using your "
                                + currentContext.getName()
                                + ". There's a key hanging behind the painting!\n" +
                                "You picked up the key. Key added to inventory.\n"
                                + (new ShowInventory().execute(state, gameState.getInventory()));
                    } else {
                        state.actionFailed();
                        return "You already cut the painting down.";
                    }
                } else {
                    return "You cut the " + currentTarget.getName() + " down using your "
                            + currentContext.getName() + ".";
                }
            } else {
                state.actionFailed();
                return "You cannot cut that.";
            }
        }

        state.actionFailed();
        return "You can't do that right now";
    }
}
