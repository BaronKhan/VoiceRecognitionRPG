package com.khan.baron.voicerecrpg.game.objects.room01;

import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;

public class GlassTable extends PhysicalObject {
    public GlassTable() {
        super("table", "drawer", "glass");
        setContext("table");
        setBreakable(true);
        setScratchable(true);
    }
}
