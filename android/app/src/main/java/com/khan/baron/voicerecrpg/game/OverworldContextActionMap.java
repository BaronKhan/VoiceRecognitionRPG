package com.khan.baron.voicerecrpg.game;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.BreakDefault;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.BreakWeaponBlunt;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.BreakWeaponNotBlunt;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.CutDefault;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.CutWeaponNotSharp;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.CutWeaponSharp;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.LookDefault;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.OpenObject;
import com.khan.baron.voicerecrpg.game.actions.overworldActions.GrabObject;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.ShowDefault;

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
        addMatchIgnore("do", "cut");

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
