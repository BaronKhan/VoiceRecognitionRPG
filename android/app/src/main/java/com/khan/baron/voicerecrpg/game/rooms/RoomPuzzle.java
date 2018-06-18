package com.khan.baron.voicerecrpg.game.rooms;
/* TODO: insert object imports */

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.items.Potion;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;
import com.khan.baron.voicerecrpg.game.objects.room01.Chair;
import com.khan.baron.voicerecrpg.game.objects.room01.GlassTable;
import com.khan.baron.voicerecrpg.game.objects.room01.Mirror;
import com.khan.baron.voicerecrpg.game.objects.room01.Pot;

public class RoomPuzzle extends Room {
    public RoomPuzzle() {
        super();
        addDescriptionWithObject(
                "You are in a room with a locked door.",
                new Door(new Troll(100), true));
        addDescriptionWithObject(
                "There is a table in the middle of the room.",
                new GlassTable());
        addDescriptionWithObjectCond(
                "An armchair is underneath the table.",
                "An armchair is in the room.",
                new Chair(),
                () -> getRoomObjectCount("table") > 0);
        addDescriptionWithObjectCond(
                "A potion is on the table.",
                "A potion is now on the floor.",
                new Potion("potion"),
                () -> getRoomObjectCount("table") > 0);
        addDescriptionWithObjectCond(
                "A knife with stains is with the potion.",
                "You see a knife with stains on it.",
                new Weapon("knife"),
                () -> getRoomObjectCount("potion") > 0);
        addDescriptionWithObject(
                "You see a vase in the corner of the room.",
                new Pot(new Item("key", Item.ItemType.ITEM_KEY, "access")));
        addDescriptionWithObject(
                "You also see a mirror on the wall.",
                new Mirror());
    }
}