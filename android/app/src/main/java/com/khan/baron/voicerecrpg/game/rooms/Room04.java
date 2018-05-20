package com.khan.baron.voicerecrpg.game.rooms;

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;

public class Room04 extends Room {
    public Room04() {
        super();
        addDescriptionWithObject(
                "You are in a room with a door in front of you.",
                new Door(new Troll(200), false));
        addDescriptionWithObject(
                "There is a knife on the floor.",
                new Weapon("knife", "sharp", "short", "metal"));
        addDescriptionWithObject(
                "There is a spoon on the floor.",
                new Weapon("spoon"));
    }
}
