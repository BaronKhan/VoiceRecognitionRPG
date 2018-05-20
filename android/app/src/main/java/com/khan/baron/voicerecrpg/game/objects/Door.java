package com.khan.baron.voicerecrpg.game.objects;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.enemies.Enemy;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.game.rooms.Room;

public class Door extends PhysicalObject {
    Room mNextRoom = null;
    Enemy mNextEnemy = null;

    private Door() {
        super("door", "gate", "lock");
        setContext("door");
        setScratchable(true);
    }

    public Door(Room nextRoom) {
        this();
        mNextRoom = nextRoom;
    }

    public Door(Enemy nextEnemy) {
        this();
        mNextEnemy = nextEnemy;
    }

    public String onOpened(GameState gameState) {
        boolean hasKey = gameState.getInventory().hasItem("key");
        if (hasKey) {
            gameState.actionSucceeded();
            gameState.getInventory().remove("key");
            if (mNextRoom != null) {
                gameState.initOverworldState(mNextRoom);
            } else if (mNextEnemy != null) {
                gameState.initBattleState(mNextEnemy);
            }
            return "You opened the door to the next room.\n\n" + gameState.getInitOutput();
        } else {
            gameState.actionFailed();
            return "The door is locked. It needs a key.";
        }
    }
}
