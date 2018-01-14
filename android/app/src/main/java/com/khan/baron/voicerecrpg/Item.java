package com.khan.baron.voicerecrpg;

/**
 * Created by Baron on 12/01/2018.
 */

public class Item {
    public enum ItemType {
        ITEM_WEAPON, ITEM_SHIELD, ITEM_HEALING, ITEM_KEY
    }

    public String mName;
    public ItemType mType;

    public Item(String name, ItemType type) {
        mName = name;
        mType = type;
    }
}
