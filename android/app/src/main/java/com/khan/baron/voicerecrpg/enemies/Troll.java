package com.khan.baron.voicerecrpg.enemies;

import com.khan.baron.voicerecrpg.GameState;

import java.util.Random;

import static java.lang.Math.min;

/**
 * Created by Baron on 12/01/2018.
 */

public class Troll extends Enemy {
    public Troll(int health) {
        super("troll", health);
    }

    @Override
    public String takeTurn(GameState state) {
        String output = "";
        int damage = 0;
        Random rng = new Random();
        switch(rng.nextInt() % 4) {
            case 0:
                output = "The troll stares at you menacingly.";
                break;
            case 1:
                output = "The troll hit you.";
                damage = 5;
                break;
            case 2:
                output = "The troll scratched you.";
                damage  = 5;
                break;
            case 3:
                output = "The troll threw you straight to the ground.";
                damage = 15;
                break;
            default:
                output = "The troll stares at you menacingly.";
                break;
        }
        state.mPlayerHealth = Math.max(0, state.mPlayerHealth - damage);
        return output;
    }
}
