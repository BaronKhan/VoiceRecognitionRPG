package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

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
                    return "You attacked the " + mCurrentEnemy.getName() + " with " + mActionContext + ".";
                } else {
                    return "There is no " + mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        return "You can't do that right now.";
    }
}
