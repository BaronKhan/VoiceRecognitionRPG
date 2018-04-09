package com.khan.baron.voicerecrpg.rooms;

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
        addRoomObject(new Door());
        addRoomObject(new Painting());
        addRoomObject(new GlassTable());
        addRoomObject(new Weapon("knife", "sharp", "metal"));
    }

    @Override
    public String getRoomDescription() {
        //TODO: generate room description using NLP
        String roomOutput = "";
        roomOutput += "You are in a room with a locked door in front of you and a glass table in"
                +"the middle of the room.";
        if (getRoomObjectCount("knife") > 0) {
            roomOutput += " There is a knife on the table.";
        }
        if (getRoomState() == StateRoom01.START.ordinal()) {
            roomOutput += " A painting of a tree hangs by a string on the wall to your left.";
        } else {
            roomOutput += " The painting that was on the wall lies on the floor.";
        }
        return roomOutput;
    }
}
