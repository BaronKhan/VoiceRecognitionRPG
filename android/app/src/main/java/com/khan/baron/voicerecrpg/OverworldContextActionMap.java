package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.overworldActions.BreakDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.BreakWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.overworldActions.BreakWeaponNotBlunt;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutWeaponNotSharp;
import com.khan.baron.voicerecrpg.actions.overworldActions.CutWeaponSharp;
import com.khan.baron.voicerecrpg.actions.sharedActions.LookDefault;
import com.khan.baron.voicerecrpg.actions.overworldActions.OpenObject;
import com.khan.baron.voicerecrpg.actions.overworldActions.GrabObject;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowDefault;

import java.util.Arrays;

public class OverworldContextActionMap extends ContextActionMap {
    public OverworldContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList(        "look",             "show",             "grab",             "open",             "cut",                      "break");
        addDefaultContextActions(           new LookDefault(),  new ShowDefault(),  new GrabObject(),   new OpenObject(),   new CutDefault(),           new BreakDefault());
        addContextActions("weapon",         null,               null,               new GrabObject(),   new OpenObject(),   new CutWeaponNotSharp(),    new BreakWeaponNotBlunt());
        addContextActions("weapon-sharp",   null,               null,               new GrabObject(),   new OpenObject(),   new CutWeaponSharp(),       new BreakWeaponNotBlunt());
        addContextActions("weapon-blunt",   null,               null,               new GrabObject(),   new OpenObject(),   new CutWeaponNotSharp(),    new BreakWeaponBlunt());

        addSynonym("observe", "look");
        addSynonym("reveal", "show");

        addMatchIgnore("jump", "look");

        addSentenceMatch(new ShowDefault(), "inventory",
                "what is in my inventory",
                "what are the contents of my bag",
                "what items do i have"
        );

        addSentenceMatch(new ShowDefault(), "actions",
                "what can i do",
                "what are my actions",
                "what are the commands",
                "what action can i do",
                "what are my options"
        );
    }
}
