package com.khan.baron.voicerecrpg.game.objects;


import android.util.Log;

import com.khan.baron.voicerecrpg.system.Entity;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.SemanticSimilarity;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class PhysicalObject extends Entity {
    //TODO: have chain of properties (e.g. if cuttable, it is also breakable, etc)
    protected boolean mIsBreakable = false;
    protected boolean mIsCuttable = false;
    protected boolean mIsScratchable = false;

    private final double AUTOASSIGN_THRESHOLD = 0.65;

    public PhysicalObject(String name, String ... description) { super(name, description); }

    public String onCut(GameState gameState, Entity currentContext) {
        gameState.actionSucceeded();
        return "You cut the " + getName() + " using your " + currentContext.getName() + ".";
    }

    public String onBroken(GameState gameState, Entity currentContext) {
        gameState.actionSucceeded();
        gameState.getCurrentRoom().removeRoomObject(this);
        return "You intentionally broke the " + this.getName() + " with your "
                +currentContext.getName()+".";
    }

    public String onBroken(GameState gameState) {
        gameState.actionSucceeded();
        gameState.getCurrentRoom().removeRoomObject(this);
        return "You intentionally broke the " + this.getName() + " with your "
                + "bare hands and it smashed to pieces. Your hands are now bruised.";
    }

    public String onScratched(GameState gameState, Entity currentContext) {
        gameState.actionSucceeded();
        return "You scratched the " + this.getName() + " using your "
                + currentContext.getName() + ", but nothing happened.";
    }

    public boolean isBreakable() {
        return mIsBreakable;
    }

    public void setBreakable(boolean mIsBreakable) {
        this.mIsBreakable = mIsBreakable;
    }

    public boolean isCuttable() {
        return mIsCuttable;
    }

    public void setCuttable(boolean mIsCuttable) {
        this.mIsCuttable = mIsCuttable;
    }

    public boolean isScratchable() {
        return mIsScratchable;
    }

    public void setScratchable(boolean mIsScratchable) {
        this.mIsScratchable = mIsScratchable;
    }

    protected void autoAssignProperties() {
        String name = getName();
        autoAssignProperty(name, "breakable", (x) -> mIsBreakable = x);
        autoAssignProperty(name, "cut", (x) -> mIsCuttable = x);
        autoAssignProperty(name, "scratch", (x) -> mIsScratchable = x);
        Log.d("PhysicalObject", name+": breakble="+mIsBreakable+"; cuttable="+mIsCuttable+
                "; scratchable="+mIsScratchable);
    }

    private void autoAssignProperty(String name, String adj, Consumer<Boolean> property) {
        if (SemanticSimilarity.getInstance().calculateScore(name, adj) > AUTOASSIGN_THRESHOLD) {
            property.accept(true);
        }
    }
}
