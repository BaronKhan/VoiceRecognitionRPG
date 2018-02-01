package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Baron on 12/01/2018.
 */

public class Inventory {
    public List<Item> mItems;

    public Inventory() {
        mItems = new ArrayList<>();
    }

    public void add(Item item) {
        mItems.add(item);
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
}
