package com.khan.baron.voicerecrpg.game.actions.overworldActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.rooms.Room;

import java.util.List;

public class GrabObject extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            List<Entity> roomObjects = gameState.getCurrentRoom().getRoomObjects();
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
        return "You can't do that right now.";
    }
}
