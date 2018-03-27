package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.items.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Baron on 12/01/2018.
 */

public class Inventory extends Context {
    public List<Item> mItems;

    public Set<String> mPastItems;

    public ContextActionMap mMap;

    public Inventory(ContextActionMap map) {
        super("inventory", "bag", "possessions", "items", "weapons", "potions", "key");
        mItems = new ArrayList<>();
        mPastItems = new HashSet<>();
        mMap = map;
    }

    public void add(Item item) {
        mItems.add(item);
        mPastItems.add(item.getName());
        mMap.setPossibleContexts(getContextList());
    }

    public int getCount(Item.ItemType type) {
        int count = 0;
        for (Item item : mItems) {
            if (item.getType() == type) { ++count; }
        }
        return count;
    }

    public int getCount(String name) {
        int count = 0;
        for (Item item : mItems) {
            if (item.getName().equals(name)) { ++count; }
        }
        return count;
    }

    public boolean hasItem(String name) { return (getCount(name) > 0); }

    public boolean hasItem(Item.ItemType type) { return (getCount(type) > 0); }

    public Item getRandomItem(Item.ItemType type) {
        int itemCount = getCount(type);
        if (itemCount < 1) { return null; }
        boolean chosenItem = false;
        while (!chosenItem) {
            for (Item item : mItems) {
                if (item.getType() == type) {
                    if (ThreadLocalRandom.current().nextInt(0, itemCount) == 0) {
                        return item;
                    }
                }
            }
        }
        return null;
    }

    public int getItemPos(String name) {
        for (int i=0; i< mItems.size(); ++i) {
            if (mItems.get(i).getName().equals(name)) { return i; }
        }
        return -1;
    }

    public void remove(String itemName) {
        for (Item item : mItems) {
            if (item.getName().equals(itemName)) {
                mItems.remove(getItemPos(itemName));
                return;
            }
        }
    }

    public List<Context> getContextList() {
        List<Context> temp = new ArrayList<>();
        for (Item i : mItems) {
            temp.add(i);
        }
        return temp;
    }

}
