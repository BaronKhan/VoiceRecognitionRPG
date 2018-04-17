package com.khan.baron.voicerecrpg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup mMethodRadioGroup;
    private RadioButton mRadioButtonWUPLIN;
    private RadioButton mRadioButtonWUP;
    private RadioButton mRadioButtonLIN;

    private Switch mOverworldSwitch;

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
                }
            }
        });

        mRadioButtonWUPLIN = (RadioButton) findViewById(R.id.radioButtonWUPLIN);
        mRadioButtonWUP = (RadioButton) findViewById(R.id.radioButtonWUP);
        mRadioButtonLIN = (RadioButton) findViewById(R.id.radioButtonLIN);
        setMethodRadioButton();

        mOverworldSwitch = (Switch) findViewById(R.id.switchOverworld);
        mOverworldSwitch.setChecked(!GameState.getStartOverworld());
        mOverworldSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                GameState.setStartOverworld(!isChecked);
            }
        });
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
            default:
                mMethodRadioGroup.check(mRadioButtonWUPLIN.getId());
        }
    }
}
