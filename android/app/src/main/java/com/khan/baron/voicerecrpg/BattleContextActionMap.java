package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.battleActions.AttackDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeapon;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.battleActions.HealDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.HealItem;
import com.khan.baron.voicerecrpg.actions.sharedActions.LookDefault;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowDefault;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

import java.util.Arrays;

public class BattleContextActionMap extends ContextActionMap {
    public BattleContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList(        "attack",                   "heal",             "show",             "look");
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
    }
}
