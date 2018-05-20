package com.khan.baron.voicerecrpg.game.rooms;

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Potion;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.Door;
import com.khan.baron.voicerecrpg.game.objects.room01.Painting;
import com.khan.baron.voicerecrpg.game.objects.room01.GlassTable;

//TODO: generate room description using NLP (the impossible task)
public class Room01 extends Room {
    public enum StateRoom01 {
        START,
        PAINTING_CUT,
        END
    }

    public Room01() {
        super();
        setRoomState(StateRoom01.START.ordinal());
        addDescriptionWithObject(
                "You are in a room with a locked door in front of you.",
                new Door(new Troll(100), true));
        addDescriptionWithObject(
                "There is a glass table in the middle of the room.",
                new GlassTable());
        addDescriptionWithObjectCond(
                "A potion has been placed on the table.",
                "A potion lays on the floor with the broken table",
                new Potion("potion"),
                () -> getRoomObjectCount("table") > 0);
        addDescriptionWithObject(
                "There is a knife on the floor.",
                new Weapon("knife", "sharp", "short", "metal"));
        addDescriptionWithObjectCond(
                "A painting of a tree hangs by a string on the left wall.",
                "The painting that was on the wall lies on the floor.",
                new Painting(),
                () -> getRoomState() == StateRoom01.START.ordinal());
    }

//    @Override
//    public String getRoomDescription() {
//
//        String roomOutput = "";
//        roomOutput += " - You are in a room with a locked door in front of you.\n";
//        if (getRoomObjectCount("table") > 0) {
//            roomOutput +=  " - There is a glass table in the middle of the room.\n";
//        }
//        if (getRoomObjectCount("knife") > 0) {
//            if (getRoomObjectCount("table") > 0) {
//                roomOutput += " - There is a knife on the table.\n";
//            } else {
//                roomOutput += " - A knife lays on the floor with the broken table.\n";
//            }
//        }
//        if (getRoomState() == StateRoom01.START.ordinal()) {
//            roomOutput += " - A painting of a tree hangs by a string on the left wall.\n";
//        } else {
//            roomOutput += " - The painting that was on the wall lies on the floor.";
//        }
//        return roomOutput;
//    }
}
