package com.khan.baron.voicerecrpg.enemies;

import java.util.Random;

/**
 * Created by Baron on 12/01/2018.
 */

public class Troll extends Enemy {
    public Troll(int health) {
        super("troll", health);
    }

    @Override
    public String takeTurn() {
        String output = "";
        Random rng = new Random();
        switch(rng.nextInt() % 3) {
            case 0:
                output = "The troll stares at you menacingly.";
                break;
            case 1:
                output = "The troll hit you.";
                break;
            case 2:
                output = "The troll scratched you.";
                break;
            default:
                output = "The troll stares at you menacingly.";
                break;
        }
        return output;
    }
}
