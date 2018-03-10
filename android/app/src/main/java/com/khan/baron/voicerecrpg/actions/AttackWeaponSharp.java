package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 31/01/2018.
 */

public class AttackWeaponSharp extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState) state);
            if (mGameMode == GameState.Mode.MODE_BATTLE) {
                if (mCurrentEnemy != null) {
                    mCurrentEnemy.decreaseHealth(17);
                    return "You attacked the " + mCurrentEnemy.getName() + " with a sharp " + mActionContext + ".";
                } else {
                    return "There is no " + mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        return "You can't do that right now.";
    }
}
