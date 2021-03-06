package com.khan.baron.voicerecrpg.game.actions.battleActions;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.game.enemies.Enemy;

/**
 * Created by Baron on 06/02/2018.
 */

public class AttackWeapon extends Action {
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof GameState) {
            GameState gameState = (GameState)state;
            if (gameState.getGameMode() == GameState.Mode.MODE_BATTLE) {
                Enemy currentEnemy = gameState.getCurrentEnemy();
                if (currentEnemy != null) {
                    currentEnemy.decreaseHealth(5);
                    state.actionSucceeded();
                    return "You attacked the " + currentEnemy.getName() + " with a "
                            + Action.getCurrentContext().getName() + ".";
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
