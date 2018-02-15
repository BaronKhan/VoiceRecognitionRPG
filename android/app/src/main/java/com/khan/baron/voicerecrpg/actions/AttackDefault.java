package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 14/01/2018.
 */

public class AttackDefault extends Action {
    public int mDamage = 5;

    public String run(GameState state) {
        getState(state);
        if (mGameMode == GameState.Mode.MODE_BATTLE) {
            if (mCurrentEnemy != null) {
                mCurrentEnemy.mHealth = Math.max(0, mCurrentEnemy.mHealth - mDamage);
                return "You attacked the " + mCurrentEnemy.mName + ".";
            } else {
                return "There is no " + mCurrentEnemy.mName + " to attack.";
            }
        }
        return "You can't do that right now.";
    }
}
