package com.khan.baron.voicerecrpg.call;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.khan.baron.voicerecrpg.CallActivity;
import com.khan.baron.voicerecrpg.R;
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
    private boolean mVideoOn = true;
    private boolean mAudioOn = true;
    private List<Contact> mParticipants = new ArrayList<>();
    private Call mCall = new Call();

    private CallActivity mCallActivity;

    public CallState(CallActivity activity) {
        mCallActivity = activity;
        mContextActionMap = new CallContextActionMap(this);
        mCommandProcess = new MultipleCommandProcess(this, mContextActionMap);
        mContextActionMap.setDefaultTarget(new Self());

        mContacts = new ArrayList<>(Arrays.asList(new Contact("fred"), new Contact("jane")));
        mContextActionMap.addPossibleTargets(mContacts);
        mContextActionMap.addPossibleTargets(Arrays.asList(new Audio(), new Video()));
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
        callContact(contact, true, true);
    }

    public void callContact(Contact contact, boolean withVideo, boolean withAudio) {
        if(!mInCall) { mContextActionMap.addPossibleTarget(mCall); }
        mInCall = true;
        if (mCallActivity != null) {
            mCallActivity.getImageViewVideo().setVisibility(View.VISIBLE);
            mCallActivity.getImageViewAudio().setVisibility(View.VISIBLE);
        }
        setVideoOn(withVideo);
        setAudioOn(withAudio);
        mParticipants.add(contact);
        if (mCallActivity != null) {
            mCallActivity.updateParticipants();
        }
    }

    public void endAllCalls() {
        mInCall = false;
        mParticipants.clear();
        if (mCallActivity != null) {
            mCallActivity.updateParticipants();
        }
        onContactRemoved();
    }

    public void endCall(Contact contact) {
        if (mParticipants.contains(contact)) {
            mParticipants.remove(contact);
            if (mCallActivity != null) {
                mCallActivity.updateParticipants();
            }
            onContactRemoved();
        }
    }

    public void onContactRemoved() {
        if (mParticipants.size() <= 0) {
            mInCall = false;
            mContextActionMap.removePossibleTarget(mCall);
            if (mCallActivity != null) {
                mCallActivity.getImageViewVideo().setVisibility(View.INVISIBLE);
                mCallActivity.getImageViewAudio().setVisibility(View.INVISIBLE);
            }
        }
    }

    public List<Contact> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(List<Contact> mParticipants) {
        this.mParticipants = mParticipants;
    }

    public boolean isVideoOn() {
        return mVideoOn;
    }

    public void setVideoOn(boolean mVideoOn) {
        this.mVideoOn = mVideoOn;
        if (mCallActivity != null) {
            mCallActivity.getImageViewVideo().setImageResource(mVideoOn
                    ? R.drawable.ic_video
                    : R.drawable.ic_video_off);
        }
    }

    public boolean isAudioOn() {
        return mAudioOn;
    }

    public void setAudioOn(boolean mAudioOn) {
        this.mAudioOn = mAudioOn;
        if (mCallActivity != null) {
            mCallActivity.getImageViewAudio().setImageResource(mAudioOn
                    ? R.drawable.ic_audio
                    : R.drawable.ic_audio_off);
        }
    }

    public boolean isInCall() {
        return mInCall;
    }

    public void setInCall(boolean mInCall) {
        this.mInCall = mInCall;
    }
}
