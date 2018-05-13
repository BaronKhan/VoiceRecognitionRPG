package com.khan.baron.voicerecrpg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.AmbiguousHandler;
import com.khan.baron.voicerecrpg.system.SemanticSimilarity;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup mMethodRadioGroup;
    private RadioButton mRadioButtonWUPLIN;
    private RadioButton mRadioButtonWUP;
    private RadioButton mRadioButtonLIN;
    private RadioButton mRadioButtonLESK;
    private RadioButton mRadioButtonFASTLESK;

    private Switch mSwitchOverworld;
    private Switch mSwitchMultSuggest;
    private Button mButtonCallDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mMethodRadioGroup = (RadioGroup) findViewById(R.id.radioGroupMethod);
        mMethodRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == mRadioButtonWUPLIN.getId()) {
                    SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_WUP_LIN);
                } else if (checkedId == mRadioButtonWUP.getId()) {
                    SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_WUP);
                } else if (checkedId == mRadioButtonLIN.getId()) {
                    SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_LIN);
                } else if (checkedId == mRadioButtonLESK.getId()) {
                    SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_LESK);
                } else if (checkedId == mRadioButtonFASTLESK.getId()) {
                    SemanticSimilarity.setStaticSimilarityMethod(SemanticSimilarity.SimilarityMethod.METHOD_FASTLESK);
                }
            }
        });

        mRadioButtonWUPLIN = (RadioButton) findViewById(R.id.radioButtonWUPLIN);
        mRadioButtonWUP = (RadioButton) findViewById(R.id.radioButtonWUP);
        mRadioButtonLIN = (RadioButton) findViewById(R.id.radioButtonLIN);
        mRadioButtonLESK = (RadioButton) findViewById(R.id.radioButtonLESK);
        mRadioButtonFASTLESK = (RadioButton) findViewById(R.id.radioButtonFASTLESK);
        setMethodRadioButton();

        mSwitchOverworld = (Switch) findViewById(R.id.switchOverworld);
        mSwitchOverworld.setChecked(!GameState.getStartOverworld());
        mSwitchOverworld.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GameState.setStartOverworld(!isChecked);
            }
        });

        mSwitchMultSuggest = (Switch) findViewById(R.id.switchMultSuggestions);
        mSwitchMultSuggest.setChecked(AmbiguousHandler.isGivingMultipleSuggestions());
        mSwitchMultSuggest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AmbiguousHandler.setGiveMultipleSuggestions(isChecked);
            }
        });

        mButtonCallDemo = (Button) findViewById(R.id.buttonCallDemo);
        mButtonCallDemo.setOnClickListener((x) -> startActivity(new Intent(this, CallActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                finishActivity(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setMethodRadioButton() {
        switch(SemanticSimilarity.getSimilarityMethod()) {
            case METHOD_WUP_LIN:
                mMethodRadioGroup.check(mRadioButtonWUPLIN.getId());
                break;
            case METHOD_WUP:
                mMethodRadioGroup.check(mRadioButtonWUP.getId());
                break;
            case METHOD_LIN:
                mMethodRadioGroup.check(mRadioButtonLIN.getId());
                break;
            case METHOD_LESK:
                mMethodRadioGroup.check(mRadioButtonLESK.getId());
                break;
            case METHOD_FASTLESK:
                mMethodRadioGroup.check(mRadioButtonFASTLESK.getId());
                break;
            default:
                mMethodRadioGroup.check(mRadioButtonWUPLIN.getId());
        }
    }
}
