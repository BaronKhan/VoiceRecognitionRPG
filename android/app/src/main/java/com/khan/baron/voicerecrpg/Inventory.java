package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.items.Item;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Baron on 12/01/2018.
 */

public class Inventory extends Context {
    public List<Item> mItems;

    public Set<String> mPastItems;

    public List<ContextActionMap> mMaps = new ArrayList<>();

    public Inventory(ContextActionMap ... maps) {
        super("inventory", "bag", "possessions", "items", "weapons", "potions", "key");
        setContext("inventory");
        mItems = new ArrayList<>();
        mPastItems = new HashSet<>();
        for (ContextActionMap map : maps) {
            map.addPossibleTarget(this);
            mMaps.add(map);
        }
    }

    public void add(Item item) {
        mItems.add(item);
        mPastItems.add(item.getName());
        for (ContextActionMap map : mMaps) {
            map.setPossibleContexts(getContextList());
        }
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
        while (true) {
            for (Item item : mItems) {
                if (item.getType() == type) {
                    if ((new Random()).nextInt() % 4 == 0) {
                        return item;
                    }
                }
            }
        }
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

    public void remove(Item item) {
        mItems.remove(item);
    }

    public List<Context> getContextList() {
        List<Context> temp = new ArrayList<>();
        for (Item i : mItems) {
            temp.add(i);
        }
        return temp;
    }

    public List<Item> getItems() { return mItems; }

}
