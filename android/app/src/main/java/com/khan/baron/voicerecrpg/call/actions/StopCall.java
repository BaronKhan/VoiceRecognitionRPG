package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.Call;
import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class StopCall extends Action {
    @Override
    public Object execute(GlobalState state, Entity currentTarget) {
        CallState callState = (CallState) state;
        if (callState.isInCall()) {
            setWantsReply(true);
            return "Are you want you want to end all the calls?";
        } else {
            return "No call is currently active.";
        }
    }

    public Object processReply(GlobalState state, String input) {
        CallState callState = (CallState) state;
        callState.endAllCalls();
        return "All calls ended.";
    }
}
