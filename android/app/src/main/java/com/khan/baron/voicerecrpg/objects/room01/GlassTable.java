package com.khan.baron.voicerecrpg.objects.room01;

import com.khan.baron.voicerecrpg.objects.PhysicalObject;

public class GlassTable extends PhysicalObject {
    public GlassTable() {
        super("table", "drawer", "glass");
        setContext("table");
        setBreakable(true);
        setScratchable(true);
    }
}
