package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.Audio;
import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.call.Video;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class Mute extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        CallState callState = (CallState) state;
        if (currentTarget instanceof Video) {
            callState.setVideoOn(false);
            return "Video muted.";
        }
        if (currentTarget instanceof Audio) {
            callState.setAudioOn(false);
            return "Audio muted";
        }
        if (currentTarget instanceof Contact) {
            return "Muted "+currentTarget.getName()+".";
        }
        return "Intent not understood.";
    }
}
