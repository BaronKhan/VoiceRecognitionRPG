package com.khan.baron.voicerecrpg.rooms;

import com.khan.baron.voicerecrpg.Entity;
import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.Inventory;
import com.khan.baron.voicerecrpg.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baron on 14/01/2018.
 */

public abstract class Room extends Entity {

    private Inventory mInventory;
    private ContextActionMap mMap;

    public static void transferRoomItemToInventory(
            Room room, Item roomItem, Inventory inventory) {
        room.removeRoomObject(roomItem);
        inventory.add(roomItem);
    }

    public static void transferInventoryItemToRoom(
            Inventory inventory, Entity inventoryItem, Room room) {
        inventory.remove(inventoryItem.getName());
        room.addRoomObject(inventoryItem);
    }

    private List<Entity> mRoomObjects = new ArrayList<>();

    private int mRoomState = -1;

    private String mDescription = "";

    public Room() {
        super("room", "environment", "surrounding");
    }

    public String getRoomDescription() { return ""; }

    public List<Entity> getRoomObjects() { return mRoomObjects; }

    public void addRoomObject(Entity obj) { mRoomObjects.add(obj); }

    public void removeRoomObject(Entity obj) { mRoomObjects.remove(obj); }

    public int getRoomObjectPos(String name) {
        for (int i=0; i< mRoomObjects.size(); ++i) {
            if (mRoomObjects.get(i).getName().equals(name)) { return i; }
        }
        return -1;
    }

    public int getRoomObjectCount(String name) {
        int count = 0;
        for (Entity roomObject : mRoomObjects) {
            if (roomObject.getName().equals(name)) { ++count; }
        }
        return count;
    }

    public int getRoomObjectCountWithDescription(String description) {
        int count = 0;
        for (Entity roomObject : mRoomObjects) {
            if (roomObject.descriptionHas(description)) { ++count; }
        }
        return count;
    }

    public boolean hasRoomObject(String name) {
        return (getRoomObjectCount(name) > 0);
    }

    public boolean hasRoomObjectWithDescription(String description) {
        return (getRoomObjectCountWithDescription(description) > 0);
    }

    public int getRoomState() {
        return mRoomState;
    }

    public void setRoomState(int mRoomState) {
        this.mRoomState = mRoomState;
    }

    //TODO: better room generation
    private void addDescription(String text) {
        addDescription(text, true);
    }

    private void addDescription(String text, boolean cond) {
        if (cond) { mDescription += " - " + text + "\n"; }
    }

    private void addDescription(String textTrue, String textFalse, boolean cond) {
        if (cond) { mDescription += " - " + textTrue + "\n"; }
        else { mDescription += " - " + textFalse + "\n"; }
    }

    private void generateObjectsFromDescription() {}
}
