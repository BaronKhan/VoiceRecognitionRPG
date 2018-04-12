package com.khan.baron.voicerecrpg.actions.battleActions;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.enemies.Enemy;

/**
 * Created by Baron on 31/01/2018.
 */

public class AttackWeaponSharp extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            Enemy currentEnemy = gameState.getCurrentEnemy();
            if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE) {
                if (currentEnemy != null) {
                    currentEnemy.decreaseHealth(17);
                    state.actionSucceeded();
                    return "You attacked the " + currentEnemy.getName() + " with a sharp "
                            + gameState.getBattleActionContext().getName() + ".";
                } else {
                    state.actionFailed();
                    return "There is no " + currentEnemy.getName() + " to attack.";
                }
            }
        }
        state.actionFailed();
        return "You can't do that right now.";
    }
}