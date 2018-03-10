package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baron on 15/02/2018.
 */

public abstract class Context {
    private String mContext = "<none>";
    protected final String mName;
    protected List<String> mDescription = new ArrayList<>();

    public Context(String name, String ... description) {
        mName = name;
        for (String word : description) {
            mDescription.add(word);
        }
    }

    public String getContext() { return mContext; }
    public void setContext(String context) { mContext = context; }

    public boolean contextIs(String adj) {
        return mDescription.contains(adj);
    }

    public String getName() { return mName; }
}
