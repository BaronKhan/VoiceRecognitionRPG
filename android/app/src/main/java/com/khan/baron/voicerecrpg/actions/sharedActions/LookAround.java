package com.khan.baron.voicerecrpg.actions.sharedActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

public class LookAround extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getGameMode() == GameState.Mode.MODE_OVERWORLD) {
                if (currentTarget == gameState.getInventory()) {
                    return new ShowInventory().execute(state, gameState.getInventory());
                } else if (currentTarget == gameState.getCurrentRoom()) {
                    if (gameState.getCurrentRoom() == null) {
                        state.actionFailed();
                    } else {
                        state.actionSucceeded();
                    }
                    return gameState.getCurrentRoom().getRoomDescription();
                } else {
                    return "Intent not understood.";
                }
            } else if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE){
                if (gameState.getCurrentEnemy() == null) {
                    state.actionFailed();
                    return "There is nothing here.";
                } else {
                    state.actionSucceeded();
                    return "You are in battle with a " + gameState.getCurrentEnemy().getName() + "!";
                }
            }
        }
        return "You can't do that right now";
    }

}
