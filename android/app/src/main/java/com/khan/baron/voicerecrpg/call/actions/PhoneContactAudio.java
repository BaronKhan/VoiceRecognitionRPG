package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class PhoneContactAudio extends Action {
    @Override
    public Object execute(GlobalState state, Entity currentTarget) {
        if (state instanceof CallState && currentTarget instanceof Contact) {
            ((CallState)state).callContact((Contact)currentTarget, false, true);
            return "Calling "+currentTarget.getName()+" (audio only)";
        }
        return "Contact not found. Who do you want to call?";
    }
}
