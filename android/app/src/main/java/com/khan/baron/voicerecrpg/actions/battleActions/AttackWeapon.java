package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 06/02/2018.
 */

public class AttackWeapon extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState) state);
            if (mGameMode == GameState.Mode.MODE_BATTLE) {
                if (mCurrentEnemy != null) {
                    mCurrentEnemy.decreaseHealth(5);
                    state.actionSucceeded();
                    return "You attacked the " + mCurrentEnemy.getName() + " with a "
                            + mActionContext.getName() + ".";
                } else {
                    state.actionFailed();
                    return "There is no " + mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        state.actionFailed();
        return "You can't do that right now.";
    }
}
