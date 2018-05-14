package com.khan.baron.voicerecrpg.call;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.MultipleCommandProcess;
import com.khan.baron.voicerecrpg.system.Self;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public class CallState extends GlobalState {
    private ContextActionMap mContextActionMap;
    private MultipleCommandProcess mCommandProcess;

    private List<Entity> mContacts;

    private boolean mInCall = false;
    private List<Contact> mParticipants = new ArrayList<>();
    private List<Contact> mVideoParticipants = new ArrayList<>();
    private List<Contact> mAudioParticipants = new ArrayList<>();

    public CallState() {
        mContextActionMap = new CallContextActionMap(this);
        mCommandProcess = new MultipleCommandProcess(this, mContextActionMap);
        mContextActionMap.setDefaultTarget(new Self());

        mContacts = new ArrayList<>(Arrays.asList(new Contact("fred"), new Contact("jane")));
        mContextActionMap.addPossibleTargets(mContacts);
    }

    public void addDictionary(URL url) throws IOException {
        mCommandProcess.addDictionary(url);
    }

    @Override
    public String updateState(String input) {
        String output = "";
        Queue<String> commandQueue = mCommandProcess.splitInput(input);
        while (!commandQueue.isEmpty()) {
            output += mCommandProcess.executeCommand(commandQueue)
                    + ((commandQueue.isEmpty()) ? "" : " >>> ");
        }
        return output;
    }

    public void callContact(Contact contact) {
        callContact(contact, true);
    }

    public void callContact(Contact contact, boolean withVideo) {
        mInCall = false;
        mParticipants.add(contact);
        (withVideo ? mVideoParticipants : mAudioParticipants).add(contact);
    }
}
