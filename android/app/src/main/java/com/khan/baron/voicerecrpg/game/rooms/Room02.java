package com.khan.baron.voicerecrpg.game.rooms;

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;

public class Room02 extends Room{
    public Room02() {
        super();
        addDescriptionWithObject(
                "You are in a room with a door in front of you.",
                new Door(new Room03(), false));
        addDescriptionWithObject(
                "There is a knife on the floor.",
                new Weapon("knife", "sharp", "short", "metal"));
    }
}
