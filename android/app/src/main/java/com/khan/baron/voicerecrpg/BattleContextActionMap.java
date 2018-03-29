package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.ContextActionMap;
import com.khan.baron.voicerecrpg.GlobalState;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeapon;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.battleActions.HealDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.HealItem;
import com.khan.baron.voicerecrpg.actions.battleActions.ShowInventory;

import java.util.Arrays;

public class BattleContextActionMap extends ContextActionMap {
    public BattleContextActionMap(GlobalState state) {
        super(state);
        mActionList = Arrays.asList("use", "attack", "heal", "show");
        addDefaultContextActions(null, new AttackDefault(), new HealDefault(), new ShowInventory());
        addContextActions("weapon", new AttackWeapon(), new AttackWeapon(), null, null);
        addContextActions("weaponSharp", new AttackWeaponSharp(), new AttackWeaponSharp(), null, null);
        addContextActions("weaponBlunt", new AttackWeaponBlunt(), new AttackWeaponBlunt(), null, null);
        addContextActions("healItem", new HealDefault(), null, new HealItem(), null);

        addSynonym("punch", "attack");
        addSynonym("recover","heal");
        addSynonym("regenerate","heal");
    }
}
