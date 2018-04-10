package com.khan.baron.voicerecrpg.objects.room01;

import com.khan.baron.voicerecrpg.objects.PhysicalObject;

public class Door extends PhysicalObject {
    public Door() {
        super("door", "gate");
        setContext("door");
        setScratchable(true);
    }
}
