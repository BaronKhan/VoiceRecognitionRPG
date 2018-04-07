package com.khan.baron.voicerecrpg.items;

import com.khan.baron.voicerecrpg.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baron on 12/01/2018.
 */

public class Item extends Context {
    public enum ItemType {
        ITEM_WEAPON, ITEM_SHIELD, ITEM_HEALING, ITEM_KEY
    }

    protected ItemType mType;


    public Item(String name, ItemType type) {
        super(name);
        mType = type;
    }

    public Item(String name, ItemType type, String ... description) {
        super(name, description);
        mType = type;
    }

    public ItemType getType() { return mType; }
}
