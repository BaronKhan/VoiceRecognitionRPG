package com.khan.baron.voicerecrpg.game;

import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.GlobalState;
import com.khan.baron.voicerecrpg.game.actions.battleActions.AttackDefault;
import com.khan.baron.voicerecrpg.game.actions.battleActions.AttackWeapon;
import com.khan.baron.voicerecrpg.game.actions.battleActions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.game.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.game.actions.battleActions.HealDefault;
import com.khan.baron.voicerecrpg.game.actions.battleActions.HealItem;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.LookDefault;
import com.khan.baron.voicerecrpg.game.actions.sharedActions.ShowDefault;

import java.util.Arrays;

public class BattleContextActionMap extends ContextActionMap {
    public BattleContextActionMap(GlobalState state) {
        super(state);
        setActionList(Arrays.asList(        "attack",                   "heal",             "show",             "look"));
        addDefaultContextActions(           new AttackDefault(),        new HealDefault(),  new ShowDefault(),  new LookDefault());
        addContextActions("weapon",         new AttackWeapon(),         null,               null,               null);
        addContextActions("weapon-sharp",   new AttackWeaponSharp(),    null,               null,               null);
        addContextActions("weapon-blunt",   new AttackWeaponBlunt(),    null,               null,               null);
        addContextActions("healing-item",   null,                       new HealItem(),     null,               null);

        addSynonym("punch", "attack");
        addSynonym("assault", "attack");
        addSynonym("observe","look");
        addSynonym("blade", "knife");

        addMatchIgnore("jump", "look");
        addMatchIgnore("jump", "attack");
        addMatchIgnore("bag", "attack");

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
