package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutWeaponSharp;
import com.khan.baron.voicerecrpg.actions.overworldActions.LookAround;
import com.khan.baron.voicerecrpg.actions.overworldActions.PickObject;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

import java.util.Arrays;

public class OverworldContextActionMap extends ContextActionMap {
    public OverworldContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList("look", "show", "pick", "open", "cut");
        addDefaultContextActions(new LookAround(), new ShowInventory(), new PickObject(), null, new CutDefault());
        addContextActions("weaponSharp", null, null, null, null, new CutWeaponSharp());

        addSynonym("observe", "look");
    }
}
