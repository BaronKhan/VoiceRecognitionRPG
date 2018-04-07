package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

public class LookAround extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getGameMode() == GameState.Mode.MODE_OVERWORLD) {
                if (currentTarget == gameState.mInventory) {
                    return new ShowInventory().run(state, gameState.mInventory);
                } else if (currentTarget == gameState.mCurrentRoom) {
                    if (gameState.mCurrentRoom == null) {
                        state.actionFailed();
                    } else {
                        state.actionSucceeded();
                    }
                    return gameState.mCurrentRoom.getRoomDescription();
                } else {
                    return "Intent not understood.";
                }
            } else if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE){
                if (gameState.mCurrentEnemy == null) {
                    state.actionFailed();
                    return "There is nothing here.";
                } else {
                    state.actionSucceeded();
                    return "You are in battle with a " + gameState.mCurrentEnemy.getName() + "!";
                }
            }
        }
        return "You can't do that right now";
    }

}
