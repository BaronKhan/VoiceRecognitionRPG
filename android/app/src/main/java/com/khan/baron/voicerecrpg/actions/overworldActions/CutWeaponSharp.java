package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.rooms.Room01;

public class CutWeaponSharp extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            Entity currentContext = gameState.getOverworldActionContext();
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                state.actionSucceeded();
                if (currentTarget instanceof PhysicalObject) {
                    PhysicalObject physicalTarget = (PhysicalObject) currentTarget;
                    if (physicalTarget.isCuttable()) {
                        if (gameState.getCurrentRoom() instanceof Room01) {
                            return physicalTarget.onCut(gameState, currentContext);
                        }
                    } else if (((PhysicalObject) currentTarget).isScratchable()) {
                        return physicalTarget.onScratched(gameState, currentContext);
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
        return "You can't do that right now.";
    }
}
