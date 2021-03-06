package com.khan.baron.voicerecrpg.game.items;

import com.khan.baron.voicerecrpg.system.Entity;

/**
 * Created by Baron on 12/01/2018.
 */

public class Item extends Entity {
    public enum ItemType {
        ITEM_WEAPON, ITEM_SHIELD, ITEM_HEALING, ITEM_KEY
    }

    protected ItemType mType;


    public Item(String name, ItemType type) {
        super(name);
        setContext(name);
        mType = type;
    }

    public Item(String name, ItemType type, String ... description) {
        super(name, description);
        setContext(name);
        mType = type;
    }

    public ItemType getType() { return mType; }
}
