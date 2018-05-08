package com.khan.baron.voicerecrpg;

import com.khan.baron.voicerecrpg.actions.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Baron on 12/01/2018.
 */

public abstract class ContextActionMap extends Entity {
    protected GlobalState mState;
    protected List<Entity> mPossibleTargets = new ArrayList<>();
    protected Entity mDefaultTarget = null;
    protected List<Entity> mPossibleContexts = new ArrayList<>();
    protected List<String> mActionList = new ArrayList<>();
    protected Map<String, Map<String, Action>> mMap = new HashMap<>();
    protected Map<String, String> mSynonymMap = new HashMap<>();
    protected Map<String, String> mIgnoreMap = new HashMap<>();
    protected SentenceMapper mSentenceMapper;

    public ContextActionMap(GlobalState state) {
        super("actions", "commands", "options");
        setContext("actions");
        mState = state;
        mPossibleTargets.add(this);
        mPossibleTargets.add(new Self());
        mSentenceMapper = new SentenceMapper(this);
    }

    public Map<String, Action> get(String action) { return mMap.get(action); }

    public List<String> getActions() { return mActionList; }

    public boolean isValidAction(String action) {
        return mActionList.contains(action);
    }

    public void setPossibleContexts(List<Entity> contextList) { mPossibleContexts = contextList; }
    public void addPossibleContext(Entity context) { mPossibleContexts.add(context); }
    public void removePossibleContext(Entity context) { mPossibleContexts.remove(context); }
    public List<Entity> getPossibleContexts() { return mPossibleContexts; }

    public void addPossibleContexts(List<Entity> contextList) {
        mPossibleContexts.addAll(contextList);
    }

    public void setPossibleTargets(List<Entity> targetsList) { mPossibleTargets = targetsList; }
    public void addPossibleTarget(Entity target) { mPossibleTargets.add(target); }
    public void removePossibleTarget(Entity target) { mPossibleTargets.remove(target); }
    public List<Entity> getPossibleTargets() { return mPossibleTargets; }

    public void addPossibleTargets(List<Entity> targetsList) {
        mPossibleTargets.addAll(targetsList);
    }

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

    public void setDefaultTarget(Entity defaultTarget) { mDefaultTarget = defaultTarget; }
    public Entity getDefaultTarget() { return mDefaultTarget; }

    public void addSynonym(String synonym, String action) {
        mSynonymMap.put(synonym,action);
    }

    public boolean hasSynonym(String synonym) {
        return mSynonymMap.keySet().contains(synonym);
    }

    public void addMatchIgnore(String ignore, String action) {
        mIgnoreMap.put(ignore,action);
    }

    public boolean wordIsIgnored(String ignore, String word) {
        return mIgnoreMap.containsKey(ignore) && mIgnoreMap.get(ignore).equals(word);
    }

    public String getSynonymAction(String synonym) {
        return (hasSynonym(synonym)) ? mSynonymMap.get(synonym) : synonym;
    }

    public boolean hasPossibleTarget(String name) {
        for (Entity target : mPossibleTargets) {
            if (target.getName().equals(name)) { return true; }
        }
        return false;
    }

    public Entity getPossibleTarget(String name) {
        for (Entity target : mPossibleTargets) {
            if (target.getName().equals(name)) { return target; }
        }
        return null;
    }

    public boolean hasPossibleContext(String name) {
        for (Entity context : mPossibleContexts) {
            if (context.getName().equals(name)) { return true; }
        }
        return false;
    }

    public void addSentenceMatch(Action action, String targetName, String ... examples) {
        mSentenceMapper.addSentenceMatch(action, targetName, examples);
    }

    public SentenceMapper getSentenceMapper() {
        return mSentenceMapper;
    }

    public void setSentenceMapper(SentenceMapper mSentenceMapper) {
        this.mSentenceMapper = mSentenceMapper;
    }
}
