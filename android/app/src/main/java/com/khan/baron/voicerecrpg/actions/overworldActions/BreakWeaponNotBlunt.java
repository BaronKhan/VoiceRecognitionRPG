package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.objects.PhysicalObject;

public class BreakWeaponNotBlunt extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        state.actionFailed();
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                if (currentTarget instanceof PhysicalObject && ((PhysicalObject) currentTarget).isBreakable()) {
                    return "The "+gameState.getOverworldActionContext().getName()+" is not blunt"
                            +" enough to break the "+currentTarget.getName();
                } else {
                    return "You cannot break the "+currentTarget.getName()+".";
                }
            } else {
                return "There is no "+currentTarget.getName()+" to break.";
            }
        }
        return "You can't do that right now";
    }
}
