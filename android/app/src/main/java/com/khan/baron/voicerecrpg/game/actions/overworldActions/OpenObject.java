package com.khan.baron.voicerecrpg.game.actions.overworldActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.game.objects.room01.Door;
import com.khan.baron.voicerecrpg.game.objects.room01.GlassTable;

public class OpenObject extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
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
                return "You cannot open the "+currentTarget.getName()+".";
            }
        }
        state.actionFailed();
        return "You can't do that right now";
    }
}
