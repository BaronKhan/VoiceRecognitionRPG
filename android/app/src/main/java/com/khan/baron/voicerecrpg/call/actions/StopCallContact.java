package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class StopCallContact extends Action {
    @Override
    public String execute(GlobalState state, Entity currentTarget) {
        CallState callState = (CallState) state;
        if (callState.isInCall()) {
            if (callState.getParticipants().contains((Contact) Action.getCurrentContext())) {
                callState.endCall((Contact) Action.getCurrentContext());
                return "Ended call with " + Action.getCurrentContext().getName() + ".";
            } else {
                return Action.getCurrentContext().getName() + " is not in the call.";
            }
        } else {
            return "No call is currently active.";
        }
    }
}
