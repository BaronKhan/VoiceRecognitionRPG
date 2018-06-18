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
                new Door(new RoomPuzzle(), true));
        addDescriptionWithObject(
                "You see a key on the floor.",
                new Item("key", Item.ItemType.ITEM_KEY, "access"));
        addDescription(
                "Try saying, 'grab the key' to acquire the key.",
                () -> getRoomObjectCount("key") > 0);
        addDescription(
                "You can also say it in other ways, like, 'pick up the key' or 'take the key'.",
                () -> getRoomObjectCount("key") > 0);
        addDescription(
                "Try opening the door with the key.");
    }
}