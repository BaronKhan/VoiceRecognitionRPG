package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 06/02/2018.
 */

public class AttackWeapon extends Action {
    public String run(GameState state) {
        getState(state);
        if (mGameMode == GameState.Mode.MODE_BATTLE) {
            if (mCurrentEnemy != null) {
                mCurrentEnemy.mHealth = Math.max(0, mCurrentEnemy.mHealth - 5);
                return "You attacked the " + mCurrentEnemy.mName + " with " + mActionContext + ".";
            } else {
                return "There is no " + mCurrentEnemy.mName + " to attack.";
            }
        }
        return "You can't do that right now.";
    }
}
