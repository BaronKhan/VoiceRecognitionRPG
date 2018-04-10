package com.khan.baron.voicerecrpg.objects;


import com.khan.baron.voicerecrpg.Context;
import com.khan.baron.voicerecrpg.GameState;

public class PhysicalObject extends Context {
    //TODO: have chain of properties (e.g. if cuttable, it is also breakable, etc)
    protected boolean mIsBreakable = false;
    protected boolean mIsCuttable = false;
    protected boolean mIsScratchable = false;

    public PhysicalObject(String name, String ... description) { super(name, description); }

    public String onCut(GameState gameState, Context currentContext) {
        gameState.actionSucceeded();
        return "You cut the " + getName() + " using your " + currentContext.getName() + ".";
    }

    public String onBroken(GameState gameState, Context currentContext) {
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

    public String onScratched(GameState gameState, Context currentContext) {
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
}
