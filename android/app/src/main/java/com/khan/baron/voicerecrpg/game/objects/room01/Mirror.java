package com.khan.baron.voicerecrpg.game.objects.room01;

import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;

import java.util.Arrays;

public class Mirror extends PhysicalObject {
    public Mirror(Item... items) {
        super("mirror", "reflection");
        setContext("mirror");
        autoAssignProperties();
        setScratchable(true);
    }
}
