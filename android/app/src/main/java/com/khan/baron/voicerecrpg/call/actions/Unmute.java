package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.Audio;
import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.call.Video;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class Unmute extends Action {
    public Object execute(GlobalState state, Entity currentTarget) {
        CallState callState = (CallState) state;
        if (currentTarget instanceof Video) {
            callState.setVideoOn(true);
            return "Video unmuted.";
        }
        if (currentTarget instanceof Audio) {
            callState.setAudioOn(true);
            return "Audio unmuted";
        }
        if (currentTarget instanceof Contact) {
            return "Unmuted "+currentTarget.getName().toUpperCase()+".";
        }
        return "Intent not understood.";
    }
}
