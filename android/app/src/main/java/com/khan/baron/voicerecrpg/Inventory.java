package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Baron on 12/01/2018.
 */

public class Inventory {
    public List<Item> mItems;

    public Set<String> mPastItems;

    public Inventory() {
        mItems = new ArrayList<>();
        mPastItems = new HashSet<>();
    }

    public void add(Item item) {
        mItems.add(item);
        mPastItems.add(item.getName());
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

}
