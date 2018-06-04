package com.khan.baron.voicerecrpg.game.rooms;
/* TODO: insert object imports */

import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.objects.Door;

public class RoomTutorial2 extends Room {
    public RoomTutorial2() {
        super();
        addDescription(
                "You are in a very small room.");
        addDescriptionWithObject(
                "There is a locked door on the opposite side.",
                new Door(new Room01(), true));
        addDescriptionWithObject(
                "You see a key on the floor.",
                new Item("key", Item.ItemType.ITEM_KEY, "access"));
        addDescription(
                "Try saying, 'pick up the key' to acquire the key.",
                () -> getRoomObjectCount("key") > 0);
        addDescription(
                "Then try opening the door.");
    }
}