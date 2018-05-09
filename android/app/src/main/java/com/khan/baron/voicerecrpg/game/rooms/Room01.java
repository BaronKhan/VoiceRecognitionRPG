package com.khan.baron.voicerecrpg.game.rooms;

import android.util.Pair;

import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.game.objects.room01.Door;
import com.khan.baron.voicerecrpg.game.objects.room01.Painting;
import com.khan.baron.voicerecrpg.game.objects.room01.GlassTable;

import java.util.List;
import java.util.function.BooleanSupplier;

import edu.stanford.nlp.util.Triple;

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
                new Door(new Troll(100)));
        addDescriptionWithObject(
                "There is a glass table in the middle of the room.",
                new GlassTable());
        addDescriptionWithObjectCond(
                "There is a knife on the table.",
                "A knife lays on the floor with the broken table.",
                new Weapon("knife", "sharp", "short", "metal"),
                () -> getRoomObjectCount("table") > 0);
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
