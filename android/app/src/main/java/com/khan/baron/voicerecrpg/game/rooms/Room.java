package com.khan.baron.voicerecrpg.game.rooms;

import android.util.Pair;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.game.Inventory;
import com.khan.baron.voicerecrpg.game.items.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import edu.stanford.nlp.util.Triple;

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

    protected List<Entity> mRoomObjects = new ArrayList<>();

    protected List<Triple<Pair<String, String>, BooleanSupplier, BooleanSupplier>> mDescriptionList
            = new ArrayList<>();

    private int mRoomState = -1;

    public Room() {
        super("room", "environment", "surrounding");
    }

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
    protected void addDescription(String text) {
        addDescription(text, () -> true);
    }

    protected void addDescription(String text, BooleanSupplier cond) {
        addDescriptionCond(text, null, cond);
    }

    protected void addDescriptionCond(String textTrue, String textFalse, BooleanSupplier cond) {
        addDescriptionWithObjectCond(textTrue, textFalse, null, cond);
    }

    protected void addDescriptionWithObject(String text, Entity object) {
        addDescriptionWithObjectCond(text, "", object, () -> true);
    }

    protected void addDescriptionWithObjectCond(
            String textTrue, String textFalse, Entity obj, BooleanSupplier cond)
    {
        BooleanSupplier objectExists = null;
        if (obj != null) {
            objectExists = () -> getRoomObjectCount(obj.getName()) > 0;
            addRoomObject(obj);
        }

        mDescriptionList.add(new Triple<>(new Pair<>(textTrue, textFalse), objectExists, cond));
    }

    public String getRoomDescription() {
        String roomOutput = "";
        for (Triple<Pair<String, String>, BooleanSupplier, BooleanSupplier> description : mDescriptionList) {
            boolean objectExists = true;
            if (description.second != null) {
                objectExists = description.second.getAsBoolean();
            }
            boolean cond = description.third.getAsBoolean();
            if (objectExists) {
                if (cond) {
                    roomOutput += " - "+description.first.first+"\n";
                } else if (description.first.second != null) {
                    roomOutput += " - "+description.first.second+"\n";
                }
            }
        }
        return roomOutput;
    }
}
