package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.Call;
import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.VoiceProcess;

public class StopCall extends Action {
    @Override
    public Object execute(GlobalState state, Entity currentTarget) {
        CallState callState = (CallState) state;
        if (callState.isInCall()) {
            if (currentTarget instanceof Contact) {
                if (callState.getParticipants().contains(currentTarget)) {
                    callState.endCall((Contact)currentTarget);
                    return "Ended call with " + currentTarget.getName() + ".";
                } else {
                    return currentTarget.getName() + " is not in the call.";
                }
            } else {
                setWantsReply(true);
                return "Are you sure you want to end all the calls?";
            }
        } else {
            return "No call is currently active.";
        }
    }

    public Object processReply(GlobalState state, String input) {
        if (VoiceProcess.replyIsYes(input)) {
            CallState callState = (CallState) state;
            callState.endAllCalls();
            return "All calls ended.";
        } else {
            return "Intent ignored.";
        }
    }
}
