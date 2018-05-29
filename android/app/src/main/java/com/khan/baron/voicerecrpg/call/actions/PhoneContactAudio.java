package com.khan.baron.voicerecrpg.call.actions;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.call.Contact;
import com.khan.baron.voicerecrpg.system.Action;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;

import java.util.List;

public class PhoneContactAudio extends Action {
    private List<Entity> mDuplicates = null;

    @Override
    public String execute(GlobalState state, Entity currentTarget) {
        if (state instanceof CallState && currentTarget instanceof Contact) {
            CallState callState = (CallState)state;
            mDuplicates = callState.hasDuplicateFirstName(currentTarget);
            if (mDuplicates == null) {
                ((CallState)state).callContact((Contact)currentTarget, false, true);
                return "Calling "+currentTarget.getName()+" (audio only)";
            } else {
                mWantsReply = true;
                mDuplicates.add(0, currentTarget);
                mCurrentTarget = currentTarget;
                String output = "Duplicate contact found. Which one?\n | ";
                for (Entity contact : mDuplicates) {
                    output += contact.getName() +" | ";
                }
                return output;
            }
        }
        return "Contact not found. Who do you want to call?";
    }

    @Override
    public Object processReply(GlobalState state, String input) {
        for (Entity contact : mDuplicates) {
            if (contact.descriptionHas(input)) {
                ((CallState)state).callContact((Contact)mCurrentTarget, false, true);
                return "Calling "+mCurrentTarget.getName()+" "+
                        mCurrentTarget.getDescription().get(0)+" (audio only)";
            }
        }
        return "Intent not understood.";
    }
}
