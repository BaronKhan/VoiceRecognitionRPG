package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.battleActions.AttackDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeapon;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.battleActions.HealDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.HealItem;
import com.khan.baron.voicerecrpg.actions.sharedActions.LookAround;
import com.khan.baron.voicerecrpg.actions.sharedActions.ShowInventory;

import java.util.Arrays;

public class BattleContextActionMap extends ContextActionMap {
    public BattleContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList("attack", "heal", "show", "look");
        addDefaultContextActions(new AttackDefault(), new HealDefault(), new ShowInventory(), new LookAround());
        addContextActions("weapon", new AttackWeapon(), null, null, null);
        addContextActions("weaponSharp", new AttackWeaponSharp(), null, null, null);
        addContextActions("weaponBlunt", new AttackWeaponBlunt(), null, null, null);
        addContextActions("healItem", null, new HealItem(), null, null);

        addSynonym("punch", "attack");
        addSynonym("recover","heal");
        addSynonym("regenerate","heal");
        addSynonym("observe","look");
    }
}
