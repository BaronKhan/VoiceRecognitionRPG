package com.khan.baron.voicerecrpg.game.enemies;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;

/**
 * Created by Baron on 12/01/2018.
 */

public class Enemy extends Entity {
    protected int mHealth;
    protected int mMaxHealth;

    public Enemy(String name, int health) {
        super(name);
        setContext("enemy");
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

    public String takeTurn(GameState state) {
        return "The " + mName + "stared at you menacingly";
    }
}
