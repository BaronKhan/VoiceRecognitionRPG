package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Baron on 15/02/2018.
 */

public abstract class Entity {
    private String mContext = "<none>";
    protected final String mName;
    protected List<String> mDescription = new ArrayList<>();

    public Entity(String name, String ... description) {
        mName = name;
        Collections.addAll(mDescription, description);
    }

    public String getContext() { return mContext; }
    public void setContext(String context) { mContext = context; }

    public boolean descriptionHas(String adj) {
        return mDescription.contains(adj);
    }

    public String getName() { return mName; }
}
