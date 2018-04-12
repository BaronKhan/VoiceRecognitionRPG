package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.overworldActions.BreakDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.BreakWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.overworldActions.BreakWeaponNotBlunt;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutWeaponNotSharp;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutWeaponSharp;
import com.khan.baron.voicerecrpg.actions.sharedActions.LookDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.OpenObject;
import com.khan.baron.voicerecrpg.actions.overworldActions.PickObject;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowDefault;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

import java.util.Arrays;

public class OverworldContextActionMap extends ContextActionMap {
    public OverworldContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList(        "look",             "show",             "pick",             "open",             "cut",                      "break");
        addDefaultContextActions(           new LookDefault(),  new ShowDefault(),  new PickObject(),   new OpenObject(),   new CutDefault(),           new BreakDefault());
        addContextActions("weapon",         null,               null,               new PickObject(),   new OpenObject(),   new CutWeaponNotSharp(),    new BreakWeaponNotBlunt());
        addContextActions("weapon-sharp",   null,               null,               new PickObject(),   new OpenObject(),   new CutWeaponSharp(),       new BreakWeaponNotBlunt());
        addContextActions("weapon-blunt",   null,               null,               new PickObject(),   new OpenObject(),   new CutWeaponNotSharp(),    new BreakWeaponBlunt());

        addSynonym("observe", "look");
    }
}
