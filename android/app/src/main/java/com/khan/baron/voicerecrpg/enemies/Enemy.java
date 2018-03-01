package com.khan.baron.voicerecrpg.enemies;

import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;

/**
 * Created by Baron on 12/01/2018.
 */

public class Enemy implements Context {
    public String mName;
    public int mHealth;
    public int mMaxHealth;

    public static GameState sGameState;

    public Enemy(String name, int health) {
        assert sGameState != null;
        mName = name;
        mHealth = health;
        mMaxHealth = health;
    }


    public String takeTurn() {
        return "The " + mName + "stared at you menacingly";
    }
}
