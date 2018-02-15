package com.khan.baron.voicerecrpg.enemies;

import com.khan.baron.voicerecrpg.Context;

/**
 * Created by Baron on 12/01/2018.
 */

public class Enemy implements Context {
    public String mName;
    public int mHealth;
    public int mMaxHealth;

    public Enemy(String name, int health) {
        mName = name;
        mHealth = health;
        mMaxHealth = health;
    }

    public String takeTurn() {
        return "The " + mName + "stared at you menacingly";
    }
}
