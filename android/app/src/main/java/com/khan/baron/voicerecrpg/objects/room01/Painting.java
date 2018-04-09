package com.khan.baron.voicerecrpg.objects.room01;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.objects.general.Cuttable;

public class Painting extends Cuttable {
    public Painting() {
        super("painting", "drawing", "portrait");
        setContext("painting");
    }
}
