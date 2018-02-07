package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baron on 12/01/2018.
 */

public abstract class Item {
    public enum ItemType {
        ITEM_WEAPON, ITEM_SHIELD, ITEM_HEALING, ITEM_KEY
    }

    protected String mName;
    protected ItemType mType;

    protected List<String> mDescription = new ArrayList<>();

    public Item(String name, ItemType type) {
        mName = name;
        mType = type;
    }

    public Item(String name, ItemType type, String ... description) {
        mName = name;
        mType = type;
        for (String word : description) {
            mDescription.add(word);
        }
    }

    public boolean itemIs(String adj) {
        return mDescription.contains(adj);
    }

    public String getName() { return mName; }

    public ItemType getType() { return mType; }
}
