package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 14/01/2018.
 */

public class AttackDefault extends Action {
    public String run(GameState state) {
        getState(state);
        if (mCurrentEnemy != null) {
            mCurrentEnemy.mHealth = Math.max(0, mCurrentEnemy.mHealth - 5);
            return "You attacked the "+mCurrentEnemy.mName+".";
        } else {
            return "There is no "+mCurrentEnemy.mName+" to attack.";
        }

    }
}
