package com.khan.baron.voicerecrpg.game.actions.overworldActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.Inventory;
import com.khan.baron.voicerecrpg.game.actions.Action;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.game.rooms.Room01;

public class CutDefault extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            Inventory inventory = gameState.getInventory();
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                for (Item item : inventory.getItems()) {
                   if (item.getContext().equals("weapon-sharp")) {
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
            state.actionFailed();
            return "What do you want to cut?";
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
