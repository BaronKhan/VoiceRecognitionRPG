package com.khan.baron.voicerecrpg.game.items;

/**
 * Created by Baron on 06/02/2018.
 */

public class Potion extends Item {
    public Potion(String name) {
        super (name, ItemType.ITEM_HEALING, "healing");
        setContext("healing-item");
    }
}
