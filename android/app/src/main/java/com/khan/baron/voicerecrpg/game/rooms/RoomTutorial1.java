package com.khan.baron.voicerecrpg.game.rooms;
/* TODO: insert object imports */

import com.khan.baron.voicerecrpg.game.objects.Door;

public class RoomTutorial1 extends Room {
    public RoomTutorial1() {
        super();
        addDescription(
            "Welcome to this Voice Recognition RPG demo!.");
        addDescriptionWithObject(
            "You are in a large room with a door in front of you.",
            new Door(new RoomTutorial2(), false));
        addDescription(
            "Try clicking the blue button below and say, 'open the door'.",
            () -> getRoomObjectCount("door") > 0);
    }
}