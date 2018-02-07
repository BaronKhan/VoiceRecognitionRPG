package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 31/01/2018.
 */

public class AttackWeaponSharp extends Action {
    public String run(GameState state) {
        getState(state);
        if (mGameMode == GameState.Mode.MODE_BATTLE) {
            if (mCurrentEnemy != null) {
                mCurrentEnemy.mHealth = Math.max(0, mCurrentEnemy.mHealth - 17);
                return "You attacked the " + mCurrentEnemy.mName + " with a sharp " + mActionContext + ".";
            } else {
                return "There is no " + mCurrentEnemy.mName + " to attack.";
            }
        }
        return "You can't do that right now.";
    }
}
