package com.khan.baron.voicerecrpg.call;

import com.khan.baron.voicerecrpg.call.actions.Mute;
import com.khan.baron.voicerecrpg.call.actions.PhoneContact;
import com.khan.baron.voicerecrpg.call.actions.PhoneContactAudio;
import com.khan.baron.voicerecrpg.call.actions.StopCall;
import com.khan.baron.voicerecrpg.call.actions.StopCallContact;
import com.khan.baron.voicerecrpg.call.actions.Unmute;
import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class CallContextActionMap extends ContextActionMap {
    public CallContextActionMap(GlobalState state) {
        super(state);
        setActionList(               "phone",                   "stop",                 "mute",         "increase");
        addDefaultContextActions(    new PhoneContact(),         new StopCall(),         new Mute(),    new Unmute());
        addContextActions("video",   new PhoneContact(),         null,                   null,          null);
        addContextActions("audio",   new PhoneContactAudio(),    null,                   null,          null);
        addContextActions("contact", null,                       new StopCallContact(),  null,          null);

        addSynonym("contact", "call");
        addSynonym("hang", "stop");
        addSynonym("unmute", "increase");
        addSynonym("silence", "mute");
    }
}
