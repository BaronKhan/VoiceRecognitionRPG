package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.AttackDefault;
import com.khan.baron.voicerecrpg.actions.AttackWeapon;
import com.khan.baron.voicerecrpg.actions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.HealDefault;
import com.khan.baron.voicerecrpg.actions.HealItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baron on 12/01/2018.
 */

public class ContextActionMap {
    protected GameState mGameState = null;
    protected List<Context> mPossibleTargets = null;
    protected Context mDefaultTarget = null;
    protected List<Context> mPossibleContexts = null;
    protected List<String> mActionList = new ArrayList<>();
    protected Map<String, Map<String, Action>> mMap = new HashMap<>();

    public ContextActionMap(GameState gameState) {
        mGameState = gameState;
        mActionList = Arrays.asList("use", "attack", "heal");
        addDefaultContextActions(null, new AttackDefault(), new HealDefault());
        addContextActions("weapon", new AttackWeapon(), new AttackWeapon(), null);
        addContextActions("weaponSharp", new AttackWeaponSharp(), new AttackWeaponSharp(), null);
        addContextActions("weaponBlunt", new AttackWeaponBlunt(), new AttackWeaponBlunt(), null);
        addContextActions("healItem", new HealDefault(), null, new HealItem());

    }

    public Map<String, Action> get(String action) { return mMap.get(action);}

    public List<String> getActions() { return mActionList; }

    public boolean isValidAction(String action) {
        return mActionList.contains(action);
    }

    public void setPossibleContexts(List<Context> contextList) { mPossibleContexts = contextList; }
    public List<Context> getPossibleContexts() { return mPossibleContexts; }

    public void setPossibleTargets(List<Context> targetsList) { mPossibleContexts = targetsList; }
    public List<Context> getPossibleTargets() { return mPossibleTargets; }

    public void addContextActions(String context, Action ... actions) {
        if (actions.length != mActionList.size()) { return; }
        Map<String, Action> tempList = new HashMap<>();
        int i=0;
        for (String actionStr: mActionList) {
            tempList.put(actionStr, actions[i]);
            ++i;
        }
        mMap.put(context, tempList);
    }

    public void addDefaultContextActions(Action ... actions) {
        addContextActions("default", actions);
    }

    public boolean isValidContext(String context) {
        return mMap.keySet().contains(context);
    }

    public void setDefaultTarget(Context defaultTarget) { mDefaultTarget = defaultTarget; }
    public Context getDefaultTarget() { return mDefaultTarget; }

}
