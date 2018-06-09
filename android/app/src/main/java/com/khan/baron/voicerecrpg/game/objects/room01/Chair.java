package com.khan.baron.voicerecrpg.game.objects.room01;

import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;

public class Chair extends PhysicalObject {
    public Chair() {
        super("chair", "seat", "furniture");
        setContext("chair");
        setBreakable(true);
        setScratchable(true);
    }
}
