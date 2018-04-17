package com.khan.baron.voicerecrpg.rooms;

import com.khan.baron.voicerecrpg.enemies.Troll;
import com.khan.baron.voicerecrpg.items.Weapon;
import com.khan.baron.voicerecrpg.objects.room01.Door;
import com.khan.baron.voicerecrpg.objects.room01.Painting;
import com.khan.baron.voicerecrpg.objects.room01.GlassTable;

public class Room01 extends Room {

    public enum StateRoom01 {
        START,
        PAINTING_CUT,
        END
    }

    public Room01() {
        super();
        setRoomState(StateRoom01.START.ordinal());
        addRoomObject(new Door(new Troll(100)));
        addRoomObject(new Painting());
        addRoomObject(new GlassTable());
        addRoomObject(new Weapon("knife", "sharp", "short", "metal"));
    }

    @Override
    public String getRoomDescription() {
        //TODO: generate room description using NLP (the impossible task)
        String roomOutput = "";
        roomOutput += " - You are in a room with a locked door in front of you.\n";
        if (getRoomObjectCount("table") > 0) {
            roomOutput +=  " - There is a glass table in the middle of the room.\n";
        }
        if (getRoomObjectCount("knife") > 0) {
            if (getRoomObjectCount("table") > 0) {
                roomOutput += " - There is a knife on the table.\n";
            } else {
                roomOutput += " - A knife lays on the floor with the broken table.\n";
            }
        }
        if (getRoomState() == StateRoom01.START.ordinal()) {
            roomOutput += " - A painting of a tree hangs by a string on the left wall.\n";
        } else {
            roomOutput += " - The painting that was on the wall lies on the floor.";
        }
        return roomOutput;
    }
}
