package com.khan.baron.voicerecrpg.game.objects.room01;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.system.Entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pot extends PhysicalObject {
    private List<Item> mItems = new ArrayList<>();

    public Pot(Item ... items) {
        super("pot", "jar", "vase");
        setContext("pot");
        setBreakable(true);
        setScratchable(true);
        setCuttable(false);
        mItems.addAll(Arrays.asList(items));
    }

    @Override
    public String onBroken(GameState gameState, Entity currentContext) {
        return onBroken(gameState);
    }

    @Override
    public String onBroken(GameState gameState) {
        gameState.actionSucceeded();
        gameState.getCurrentRoom().removeRoomObject(this);
        String output = "You broke the pot. ";
        if (mItems.size() > 0) {
            output += "The following items were inside:\n";
            for (Item item : mItems) {
                gameState.getInventory().add(item);
                output += " - "+item.getName()+"\n";
            }
            output += "All items were added to inventory.\n"+
                    (new ShowInventory().execute(gameState, gameState.getInventory()));
        }
        return output;
    }
}
