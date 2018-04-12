package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.objects.PhysicalObject;

public class BreakWeaponBlunt extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                if (currentTarget instanceof PhysicalObject && ((PhysicalObject) currentTarget).isBreakable()) {
                    return ((PhysicalObject) currentTarget).onBroken(gameState, gameState.getContext());
                } else {
                    state.actionFailed();
                    return "You cannot break the "+currentTarget.getName()+".";
                }
            } else {
                state.actionFailed();
                return "There is no "+currentTarget.getName()+" to break.";
            }
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}