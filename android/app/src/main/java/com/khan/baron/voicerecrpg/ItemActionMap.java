package com.khan.baron.voicerecrpg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baron on 12/01/2018.
 */

public class ItemActionMap {
    public GameState mGameState = null;
    public Map<String,List<Action>> mMap = new HashMap<>();

    public ItemActionMap(GameState gameState) {
        mGameState = gameState;
        //  action                                              default,                Weapon,                             WeaponSharp,                WeaponBlunt,                HealItem
        mMap.put("use", new ArrayList<Action>(Arrays.asList(    null,                   new AttackWeapon(),                 new AttackWeaponSharp(),    new AttackWeaponBlunt(),    new HealItem() )));
        mMap.put("attack", new ArrayList<Action>(Arrays.asList( new AttackDefault(),    new AttackWeapon(),                 new AttackWeaponSharp() ,   new AttackWeaponBlunt(),    null)));
        mMap.put("heal", new ArrayList<Action>(Arrays.asList(   new HealDefault(),      null,                               null,                       null,                       new HealItem() )));
    }

    public List<Action> get(String key) {
        return mMap.get(key);
    }

    public boolean isValidAction(String action) {
        return (mMap.get(action) != null);
    }
}
