package com.khan.baron.voicerecrpg.actions.overworldActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.objects.room01.Door;
import com.khan.baron.voicerecrpg.objects.room01.GlassTable;

public class OpenObject extends Action {
    public String execute(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState) state;
            if (gameState.getCurrentRoom().hasRoomObject(currentTarget.getName())) {
                if (currentTarget instanceof Door) {
                    state.actionSucceeded();
                    return ((Door) currentTarget).onOpened(gameState);
                } else if (currentTarget instanceof GlassTable) {
                    state.actionSucceeded();
                    return "You opened the drawer of the glass table but nothing is inside. You"
                            +" closed it again.";
                } else {
                    state.actionFailed();
                    return "You cannot open the "+currentTarget.getName()+".";
                }
            } else {
                state.actionFailed();
                return "There is no "+currentTarget.getName()+" to open.";
            }
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
