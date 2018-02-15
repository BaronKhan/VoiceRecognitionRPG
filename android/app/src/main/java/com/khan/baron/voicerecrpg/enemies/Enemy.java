package com.khan.baron.voicerecrpg.enemies;

/**
 * Created by Baron on 12/01/2018.
 */

public class Enemy {
    public String mName;
    public int mHealth;
    public int mMaxHealth;

    public Enemy(String name, int health) {
        mName = name;
        mHealth = health;
        mMaxHealth = health;
    }
}
