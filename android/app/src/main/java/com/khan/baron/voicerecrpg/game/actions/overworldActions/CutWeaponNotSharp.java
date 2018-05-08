package com.khan.baron.voicerecrpg.game.actions.overworldActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.actions.Action;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;

public class CutWeaponNotSharp extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        state.actionFailed();
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                if (currentTarget instanceof PhysicalObject && ((PhysicalObject) currentTarget).isCuttable()) {
                    String output = "The "+gameState.getOverworldActionContext().getName()+" is not sharp"
                            +" enough to cut the "+currentTarget.getName()+".";
                    if (gameState.getCurrentRoom().hasRoomObjectWithDescription("sharp")) {
                        output += "\nThere may be a sharper object you can pick up in the room.";
                    }
                    return output;
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

