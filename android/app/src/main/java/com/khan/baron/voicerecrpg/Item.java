package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 12/01/2018.
 */

public abstract class Item {
    public enum ItemType {
        ITEM_WEAPON, ITEM_SHIELD, ITEM_HEALING, ITEM_KEY
    }

    protected String mName;
    protected ItemType mType;

    public Item(String name, ItemType type) {
        mName = name;
        mType = type;
    }

    public String getName() { return mName; }

    public ItemType getType() { return mType; }
}
