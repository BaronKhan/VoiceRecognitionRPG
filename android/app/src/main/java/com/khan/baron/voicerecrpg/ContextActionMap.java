package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.Action;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeapon;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponBlunt;
import com.khan.baron.voicerecrpg.actions.battleActions.AttackWeaponSharp;
import com.khan.baron.voicerecrpg.actions.battleActions.HealDefault;
import com.khan.baron.voicerecrpg.actions.battleActions.HealItem;
import com.khan.baron.voicerecrpg.actions.battleActions.ShowInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baron on 12/01/2018.
 */

public abstract class ContextActionMap {
    protected GlobalState mState = null;
    protected List<Context> mPossibleTargets = null;
    protected Context mDefaultTarget = null;
    protected List<Context> mPossibleContexts = null;
    protected List<String> mActionList = new ArrayList<>();
    protected Map<String, Map<String, Action>> mMap = new HashMap<>();
    protected Map<String, String> mSynonymMap = new HashMap<>();

    public ContextActionMap(GlobalState state) { mState = state; }

    public Map<String, Action> get(String action) { return mMap.get(action); }

    public List<String> getActions() { return mActionList; }

    public boolean isValidAction(String action) {
        return mActionList.contains(action);
    }

    public void setPossibleContexts(List<Context> contextList) { mPossibleContexts = contextList; }
    public List<Context> getPossibleContexts() { return mPossibleContexts; }

    public void setPossibleTargets(List<Context> targetsList) { mPossibleTargets = targetsList; }
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

    public void addSynonym(String synonym, String action) {
        mSynonymMap.put(synonym,action);
    }

    public boolean hasSynonym(String synonym) {
        return mSynonymMap.keySet().contains(synonym);
    }

    public String getSynonymAction(String synonym) {
        return (hasSynonym(synonym)) ? mSynonymMap.get(synonym) : synonym;
    }

}
