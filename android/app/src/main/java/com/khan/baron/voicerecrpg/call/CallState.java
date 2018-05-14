package com.khan.baron.voicerecrpg.call;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.system.MultipleCommandProcess;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CallState extends GlobalState {
    private ContextActionMap mContextActionMap;
    private MultipleCommandProcess mCommandProcess;

    private List<Entity> mContacts;

    public CallState() {
        mContextActionMap = new CallContextActionMap(this);
        mCommandProcess = new MultipleCommandProcess(this, mContextActionMap);

        mContacts = new ArrayList<>(Arrays.asList(new Contact("Dan"), new Contact("Fred")));
        mContextActionMap.addPossibleContexts(mContacts);
    }

    public void addDictionary(URL url) throws IOException {
        mCommandProcess.addDictionary(url);
    }

    public void updateState() {}

}
