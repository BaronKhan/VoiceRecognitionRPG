package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;

/**
 * Created by Baron on 14/01/2018.
 */

public class AttackDefault extends Action {
    public int mDamage = 5;

    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            getState((GameState)state);
            if (mGameMode == GameState.Mode.MODE_BATTLE) {
                if (mCurrentEnemy != null) {
                    mCurrentEnemy.decreaseHealth(mDamage);
                    return "You attacked the " + mCurrentEnemy.getName() + ".";
                } else {
                    return "There is no " + mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        return "You can't do that right now.";
    }
}
