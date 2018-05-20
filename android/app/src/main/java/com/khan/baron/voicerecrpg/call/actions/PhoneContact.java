package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

public class PhoneContact extends Action {
    @Override
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof CallState && currentTarget instanceof Contact) {
            ((CallState)state).callContact((Contact)currentTarget);
            return "Calling "+currentTarget.getName();
        }
        return "Contact not found. Who do you want to call?";
    }
}
