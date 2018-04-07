package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;

/**
 * Created by Baron on 31/01/2018.
 */

public class AttackWeaponSharp extends Action {
    public String run(GlobalState state, Context currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE) {
                if (gameState.mCurrentEnemy != null) {
                    gameState.mCurrentEnemy.decreaseHealth(17);
                    state.actionSucceeded();
                    return "You attacked the " + gameState.mCurrentEnemy.getName() + " with a sharp "
                            + gameState.getBattleActionContext().getName() + ".";
                } else {
                    state.actionFailed();
                    return "There is no " + gameState.mCurrentEnemy.getName() + " to attack.";
                }
            }
        }
        state.actionFailed();
        return "You can't do that right now.";
    }
}
