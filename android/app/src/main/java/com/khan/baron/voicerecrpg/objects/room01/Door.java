package com.khan.baron.voicerecrpg.objects.room01;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.objects.general.Cuttable;

public class Door extends Cuttable {
    public Door() {
        super("door", "gate");
        setContext("door");
    }
}
