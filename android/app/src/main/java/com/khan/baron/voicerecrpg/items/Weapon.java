package com.khan.baron.voicerecrpg.items;

/**
 * Created by Baron on 31/01/2018.
 */

public class Weapon extends Item {
    public double mDamageModifier;

    public Weapon(String name) {
        super (name, ItemType.ITEM_WEAPON);
        setContext("weapon");
    }

    public Weapon(String name, String ... description) {
        this(name, 1.0, description);
    }

    public Weapon(String name, double damageMod, String ... description) {
        super (name, ItemType.ITEM_WEAPON, description);
        mDamageModifier = damageMod;
        if (mDescription.contains("sharp")) {
            setContext("weapon-sharp");
        } else if (mDescription.contains("blunt")) {
            setContext("weapon-blunt");
        } else {
            setContext("weapon");
        }
    }
}
