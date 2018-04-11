package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.items.Item;
import com.khan.baron.voicerecrpg.rooms.Room;

import java.util.List;

public class PickObject extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            List<Context> roomObjects = gameState.getCurrentRoom().getRoomObjects();
            if (roomObjects.contains(currentTarget) && currentTarget instanceof Item) {
                Room.transferRoomItemToInventory(
                        gameState.getCurrentRoom(),(Item)currentTarget, gameState.getInventory());

                state.actionSucceeded();
                return "You picked up the "+currentTarget.getName()+". Added to inventory.\n"
                        +(new ShowInventory().execute(state, gameState.getInventory()));
            }
            state.actionFailed();
            return "You can't pick that up right now.";
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
