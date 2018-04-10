package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.objects.room01.Painting;
import com.khan.baron.voicerecrpg.rooms.Room01;

public class CutDefault extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            Inventory inventory = gameState.getInventory();
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                for (Item item : inventory.getItems()) {
                   if (item.getContext().equals("weaponSharp")) {
                       if (currentTarget instanceof PhysicalObject) {
                           if (((PhysicalObject) currentTarget).isCuttable()) {
                               if (gameState.getCurrentRoom() instanceof Room01) {
                                   return ((PhysicalObject) currentTarget).onCut(gameState, item);
                               } else {
                                   return ((PhysicalObject) currentTarget).onCut(gameState, item);
                               }
                           } else if (((PhysicalObject) currentTarget).isScratchable()) {
                               return ((PhysicalObject) currentTarget).onScratched(gameState, item);
                           } else {
                               state.actionFailed();
                               return "You cannot cut that.";
                           }
                       } else {
                           state.actionFailed();
                           return "You cannot cut that.";
                       }
                   }
                }
                state.actionFailed();
                return "You don't have anything to cut the "+currentTarget.getName()+" with.";
            }
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
