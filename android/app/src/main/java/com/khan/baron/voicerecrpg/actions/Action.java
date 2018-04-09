package com.khan.baron.voicerecrpg.actions;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.enemies.Enemy;
import com.khan.baron.voicerecrpg.rooms.Room;

/**
 * Created by Baron on 11/01/2018.
 */

public abstract class Action {
    public abstract Object execute(GlobalState state, Context currentTarget);
}
