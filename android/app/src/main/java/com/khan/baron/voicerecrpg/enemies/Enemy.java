package com.khan.baron.voicerecrpg.enemies;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 12/01/2018.
 */

public class Enemy extends Context {
    protected final String mName;
    protected int mHealth;
    protected int mMaxHealth;

    public static GameState sGameState;

    public Enemy(String name, int health) {
        setContext("enemy");
        assert sGameState != null;
        mName = name;
        mHealth = health;
        mMaxHealth = health;
    }

    @Override
    public String getName() { return mName; }

    public int getHealth() { return mHealth; }
    public int getMaxHealth() { return mMaxHealth; }

    public void setHealth(int health) { mHealth = health; }
    public void setMaxHealth(int maxHealth) { mMaxHealth = maxHealth; }

    public void increaseHealth(int inc) {
        mHealth = Math.max(0, Math.min(mMaxHealth, mHealth + inc));
    }

    public void decreaseHealth(int dec) { increaseHealth(-dec); }

    public String takeTurn() {
        return "The " + mName + "stared at you menacingly";
    }
}
