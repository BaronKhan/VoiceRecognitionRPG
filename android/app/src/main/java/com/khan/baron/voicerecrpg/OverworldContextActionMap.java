package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.overworldActions.LookAround;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

import java.util.Arrays;

public class OverworldContextActionMap extends ContextActionMap {
    public OverworldContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList("look", "show", "pick");
        addDefaultContextActions(new LookAround(), new ShowInventory(), null);

        addSynonym("observe", "look");
    }
}
