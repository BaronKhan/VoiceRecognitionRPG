package com.khan.baron.voicerecrpg;

import android.app.Activity;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import edu.mit.jwi.IDictionary;

public class MultipleCommandProcess {
    private VoiceProcess mVoiceProcess;

    public MultipleCommandProcess(VoiceProcess voiceProcess) { mVoiceProcess = voiceProcess; }

    public MultipleCommandProcess(
            Activity mainActivity, GlobalState state, ContextActionMap contextActionMap) {
        mVoiceProcess = new VoiceProcess(mainActivity, state, contextActionMap);
    }

    public Queue<String> splitInput(String input) {
        return new LinkedList<>(Arrays.asList(input.split(" and then | and | then ")));
    }

    public Object executeCommand(Queue<String> queue) {
        if (queue == null || queue.size() <= 0) { return null; }

        String command = queue.peek();
        if (command != null) {
            queue.remove();
            return mVoiceProcess.processInput(command);
        } else { return null; }
    }

    public void addDictionary(URL url) throws IOException {
        if (mVoiceProcess != null) { mVoiceProcess.addDictionary(url); }
    }

    public Entity getActionContext() {
        if (mVoiceProcess != null) { return mVoiceProcess.getActionContext(); }
        else { return null; }
    }

    public IDictionary getDictionary() {
        if (mVoiceProcess != null) { return mVoiceProcess.getDictionary(); }
        else { return null; }
    }
}
