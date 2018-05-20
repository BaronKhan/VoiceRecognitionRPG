package com.khan.baron.voicerecrpg.game.rooms;

import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;

public class Room03 extends Room{
    public Room03() {
        super();
        addDescriptionWithObject(
                "You are in a room with a door in front of you.",
                new Door(new Room04(), false));
        addDescriptionWithObject(
                "There is a spoon on the floor.",
                new Weapon("spoon"));
    }
}
