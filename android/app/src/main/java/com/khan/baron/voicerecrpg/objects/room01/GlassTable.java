package com.khan.baron.voicerecrpg.objects.room01;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.objects.general.Cuttable;

public class GlassTable extends Cuttable {
    public GlassTable() {
        super("table", "drawer", "glass");
        setContext("table");
    }
}
