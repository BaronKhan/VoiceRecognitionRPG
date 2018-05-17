package com.khan.baron.voicerecrpg;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.system.AmbiguousHandler;
import com.khan.baron.voicerecrpg.system.ContextActionMap;
import com.khan.baron.voicerecrpg.system.SemanticSimilarity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup mMethodRadioGroup;
    private RadioButton mRadioButtonWUPLIN;
    private RadioButton mRadioButtonWUP;
    private RadioButton mRadioButtonLIN;
    private RadioButton mRadioButtonLESK;
    private RadioButton mRadioButtonFASTLESK;
    private RecyclerView mRecyclerViewSynonyms;

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
                    setSimilarityMethods(SemanticSimilarity.SimilarityMethod.METHOD_WUP,
                            SemanticSimilarity.SimilarityMethod.METHOD_LIN);
                } else if (checkedId == mRadioButtonWUP.getId()) {
                    setSimilarityMethods(SemanticSimilarity.SimilarityMethod.METHOD_WUP, null);
                } else if (checkedId == mRadioButtonLIN.getId()) {
                    setSimilarityMethods(SemanticSimilarity.SimilarityMethod.METHOD_LIN, null);
                } else if (checkedId == mRadioButtonLESK.getId()) {
                    setSimilarityMethods(SemanticSimilarity.SimilarityMethod.METHOD_LESK, null);
                } else if (checkedId == mRadioButtonFASTLESK.getId()) {
                    setSimilarityMethods(SemanticSimilarity.SimilarityMethod.METHOD_FASTLESK, null);
                }
            }

            private void setSimilarityMethods(SemanticSimilarity.SimilarityMethod method1,
                                             SemanticSimilarity.SimilarityMethod method2)
            {
                SemanticSimilarity.setSimilarityMethodEnum(1, method1);
                SemanticSimilarity.setSimilarityMethodEnum(2, method2);
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

        mRecyclerViewSynonyms = (RecyclerView) findViewById(R.id.recyclerViewSynonymMap);
        mRecyclerViewSynonyms.setAdapter(new SynonymsAdapter());
        mRecyclerViewSynonyms.setLayoutManager(new LinearLayoutManager(this));
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
        SemanticSimilarity.SimilarityMethod method1 = SemanticSimilarity.getSimilarityMethod(1);
        SemanticSimilarity.SimilarityMethod method2 = SemanticSimilarity.getSimilarityMethod(2);
        if (method1 == SemanticSimilarity.SimilarityMethod.METHOD_WUP &&
                method2 == SemanticSimilarity.SimilarityMethod.METHOD_LIN)
        {
            mMethodRadioGroup.check(mRadioButtonWUPLIN.getId());
        } else if (method1 == SemanticSimilarity.SimilarityMethod.METHOD_WUP) {
            mMethodRadioGroup.check(mRadioButtonWUP.getId());
        } else if (method1 == SemanticSimilarity.SimilarityMethod.METHOD_LIN) {
            mMethodRadioGroup.check(mRadioButtonLIN.getId());
        } else if (method1 == SemanticSimilarity.SimilarityMethod.METHOD_LESK) {
            mMethodRadioGroup.check(mRadioButtonLESK.getId());
        } else if (method1 == SemanticSimilarity.SimilarityMethod.METHOD_FASTLESK) {
            mMethodRadioGroup.check(mRadioButtonFASTLESK.getId());
        } else {
            mMethodRadioGroup.check(mRadioButtonWUPLIN.getId());
        }
    }

    // Taken from example at: https://guides.codepath.com/android/using-the-recyclerview
    public class SynonymsAdapter extends RecyclerView.Adapter<SynonymsAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView synonymTextView;
            public Button deleteButton;

            public ViewHolder(View itemView) {
                super(itemView);

                synonymTextView = (TextView) itemView.findViewById(R.id.synonym_pair);
                deleteButton = (Button) itemView.findViewById(R.id.delete_button);
            }
        }

        @Override
        public SynonymsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.layout_synonyn_map, parent, false);

            ViewHolder viewHolder = new ViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(SynonymsAdapter.ViewHolder viewHolder, int position) {
            List list = Arrays.asList(ContextActionMap.getUserSynonyms().entrySet().toArray());
            Map.Entry pair = (Map.Entry)list.get(position);

            TextView textView = viewHolder.synonymTextView;
            textView.setText(pair.getKey()+" --> "+pair.getValue());
            Button button = viewHolder.deleteButton;
            button.setText(getString(R.string.delete));
        }

        @Override
        public int getItemCount() {
            return ContextActionMap.getUserSynonyms().size();
        }
    }
}
