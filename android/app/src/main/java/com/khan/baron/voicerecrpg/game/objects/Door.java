package com.khan.baron.voicerecrpg.game.objects;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.enemies.Enemy;
import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.game.rooms.Room;

public class Door extends PhysicalObject {
    private Room mNextRoom = null;
    private Enemy mNextEnemy = null;

    private boolean mNeedsKey;

    private Door(boolean needsKey) {
        super("door", "gate", "lock");
        setContext("door");
        setScratchable(true);
        mNeedsKey = needsKey;
    }

    public static Door toNextRoomFrom(Class nextRoom, boolean needsKey) {
        try {
            if (Room.getRoomConnections().containsKey(nextRoom)) {
                return new Door((Room)(Room.getRoomConnections().get(nextRoom).newInstance()),
                        needsKey);
            } else {
                return new Door(new Troll(100), needsKey);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            return new Door(new Troll(100), needsKey);
        }
    }

    public Door(Room nextRoom, boolean needsKey) {
        this(needsKey);
        mNextRoom = nextRoom;
    }

    public Door(Enemy nextEnemy, boolean needsKey) {
        this(needsKey);
        mNextEnemy = nextEnemy;
    }

    public String onOpened(GameState gameState) {
        boolean hasKey = (!mNeedsKey) || gameState.getInventory().hasItem("key");
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
