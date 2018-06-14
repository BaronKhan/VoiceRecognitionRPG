package com.khan.baron.voicerecrpg.game.items;

import com.khan.baron.voicerecrpg.system.SemanticSimilarity;

/**
 * Created by Baron on 31/01/2018.
 */

public class Weapon extends Item {
    public double mDamageModifier;

    private final double PROPERTY_THRESHOLD=0.85;

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
        if (getDescription().contains("sharp") ||
                SemanticSimilarity.getInstance().calculateScore(mName, "sharp") > PROPERTY_THRESHOLD)
        {
            setContext("weapon-sharp");
        } else if (getDescription().contains("blunt") ||
                SemanticSimilarity.getInstance().calculateScore(mName, "blunt") > PROPERTY_THRESHOLD)
        {
            setContext("weapon-blunt");
        } else {
            setContext("weapon");
        }
    }
}
