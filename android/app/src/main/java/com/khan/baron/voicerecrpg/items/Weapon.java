package com.khan.baron.voicerecrpg.items;

/**
 * Created by Baron on 31/01/2018.
 */

public class Weapon extends Item {
    public Weapon(String name) {
        super (name, ItemType.ITEM_WEAPON);
    }

    public Weapon(String name, String ... description) {
        super (name, ItemType.ITEM_WEAPON, description);
    }
}
