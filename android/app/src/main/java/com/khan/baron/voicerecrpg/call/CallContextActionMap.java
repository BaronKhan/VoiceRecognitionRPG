package com.khan.baron.voicerecrpg.call;

import com.khan.baron.voicerecrpg.call.actions.CallContact;
import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.GlobalState;

import java.util.Arrays;

public class CallContextActionMap extends ContextActionMap {
    public CallContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList(        "call",                     "stop",                 "mute");
        addDefaultContextActions(            new CallContact(),         null,                   null);

        addSynonym("contact", "call");
        addSynonym("hang", "stop");
    }
}
