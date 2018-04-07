package com.khan.baron.voicerecrpg.rooms;

import com.khan.baron.voicerecrpg.Context;

/**
 * Created by Baron on 14/01/2018.
 */

public abstract class Room extends Context {
    public Room() {
        super("room", "environment", "surrounding");
    }

    public String getRoomDescription() { return ""; }
}
