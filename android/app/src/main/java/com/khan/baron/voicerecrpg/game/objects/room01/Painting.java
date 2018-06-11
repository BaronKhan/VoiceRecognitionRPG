package com.khan.baron.voicerecrpg.game.objects.room01;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.ShowInventory;
import com.khan.baron.voicerecrpg.game.items.Item;
import com.khan.baron.voicerecrpg.game.objects.PhysicalObject;
import com.khan.baron.voicerecrpg.game.rooms.Room01;

public class Painting extends PhysicalObject {
    public Painting() {
        super("painting", "drawing", "portrait");
        setContext("painting");
        autoAssignProperties();
        setBreakable(false);    //for gameplay purposes
    }

    @Override
    public String onCut(GameState gameState, Entity currentContext) {
        if (gameState.getCurrentRoom().getRoomState() != Room01.StateRoom01.PAINTING_CUT.ordinal()) {
            gameState.actionSucceeded();
            gameState.getInventory().add(new Item("key", Item.ItemType.ITEM_KEY, "access"));
            gameState.getCurrentRoom().setRoomState(
                    Room01.StateRoom01.PAINTING_CUT.ordinal());
            return "You cut the painting of the tree down using your "
                    + currentContext.getName()
                    + ". There's a key hanging behind the painting!\n" +
                    "You picked up the key. Key added to inventory.\n"
                    + (new ShowInventory().execute(gameState, gameState.getInventory()));
        } else {
            gameState.actionFailed();
            return "You already cut the painting down.";
        }
    }
}
