package com.khan.baron.voicerecrpg.game.actions.sharedActions;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.Inventory;
import com.khan.baron.voicerecrpg.game.actions.Action;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.game.rooms.Room;

public class LookDefault extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            state.actionFailed();
            if (currentTarget instanceof Inventory) {
                return new ShowInventory().execute(state, currentTarget);
            } else if (currentTarget instanceof ContextActionMap) {
                return new ShowActions().execute(state, currentTarget);
            } else if (currentTarget instanceof PhysicalObject) {
                return "It looks like an ordinary "+currentTarget.getName()+".";
            } else {
                if (gameState.getGameMode() == GameState.Mode.MODE_OVERWORLD) {
                    if (currentTarget instanceof Room) {
                        return gameState.getCurrentRoom().getRoomDescription();
                    }
                    else {
                        return "Intent not understood.";
                    }
                } else if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE) {
                    if (gameState.getCurrentEnemy() != null) {
                        return "You are in battle with a " + gameState.getCurrentEnemy().getName() + "!";
                    } else {
                        return "There is nothing here.";
                    }
                }
            }
        }
        return "You can't do that right now";
    }

}
