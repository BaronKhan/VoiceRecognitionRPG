package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.objects.PhysicalObject;

public class CutWeaponNotSharp extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                if (currentTarget instanceof PhysicalObject && ((PhysicalObject) currentTarget).isCuttable()) {
                    return "The "+gameState.getOverworldActionContext().getName()+" is not sharp"
                            +" enough to cut the "+currentTarget.getName();
                } else {
                    return "You cannot cut the "+currentTarget.getName()+".";
                }
            } else {
                return "you cannot cut that.";
            }
        }
        return "You can't do that right now";
    }
}

