package com.khan.baron.voicerecrpg;

import android.app.Activity;
import android.content.res.AssetManager;

import java.net.URL;

public class GameState {
    Activity mMainActivity;

    public GameState(Activity mainActivity) {
        mMainActivity = mainActivity;
    }

    public String updateState(String input) {
        String path = mMainActivity.getAssets().toString();
        return "You are in an output scenario.";
    }
}
