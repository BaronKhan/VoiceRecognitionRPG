package com.khan.baron.voicerecrpg.game.rooms;

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;

public class RoomMoreUtensils extends Room {
    public RoomMoreUtensils() {
        super();
        addDescriptionWithObject(
                "You are in a room with a door in front of you.",
                new Door(new Troll(200), false));
        addDescriptionWithObject(
                "There is a knife on the floor.",
                new Weapon("knife", "sharp", "short", "metal"));
        addDescriptionWithObject(
                "There is a fork on the floor.",
                new Weapon("fork"));
        addDescriptionWithObject(
                "There is a spoon on the floor.",
                new Weapon("spoon"));
        addDescriptionWithObject(
                "There is a strange instrument on the floor.",
                new Weapon("instrument"));
    }
}
