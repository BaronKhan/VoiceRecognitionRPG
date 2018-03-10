package com.khan.baron.voicerecrpg.items;

/**
 * Created by Baron on 31/01/2018.
 */

public class Weapon extends Item {
    public Weapon(String name) {
        super (name, ItemType.ITEM_WEAPON);
        setContext("weapon");
    }

    public Weapon(String name, String ... description) {
        super (name, ItemType.ITEM_WEAPON, description);
        if (mDescription.contains("sharp")) {
            setContext("weaponSharp");
        } else if (mDescription.contains("blunt")) {
            setContext("weaponBlunt");
        } else {
            setContext("weapon");
        }
    }
}
