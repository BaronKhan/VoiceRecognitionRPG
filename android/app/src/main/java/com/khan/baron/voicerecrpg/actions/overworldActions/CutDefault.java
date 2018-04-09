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
import com.khan.baron.voicerecrpg.rooms.Room;
import com.khan.baron.voicerecrpg.rooms.Room01;

import java.util.List;

public class CutDefault extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            Inventory inventory = gameState.getInventory();
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())
                    && currentTarget instanceof Cuttable) {
               for (Item item : inventory.getItems()) {
                   if (item.getContext().equals("weaponSharp")) {
                       state.actionSucceeded();
                       if (gameState.getCurrentRoom() instanceof Room01
                               && currentTarget instanceof Painting) {
                           if (gameState.getCurrentRoom().getRoomState() != Room01.StateRoom01.PAINTING_CUT.ordinal()) {
                               inventory.add(new Item("key", Item.ItemType.ITEM_KEY, "access"));
                               gameState.getCurrentRoom().setRoomState(
                                       Room01.StateRoom01.PAINTING_CUT.ordinal());
                               return "You cut the painting of the tree down using your "
                                       + item.getName() + ". There's a key hanging behind the painting!\n" +
                                       " You picked up the key. Key added to inventory.\n"
                                       + (new ShowInventory().execute(state, gameState.getInventory()));
                           } else {
                               state.actionFailed();
                               return "You already cut the painting down.";
                           }
                       }
                       else {
                           return "You cut the " + currentTarget.getName() + " using your "
                                   +item.getName()+", but nothing happened.";
                       }
                   }
               }
               state.actionFailed();
               return "You don't have anything to cut the "+currentTarget.getName()+" with.";
            } else {
                state.actionFailed();
                return "You cannot cut that.";
            }
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
