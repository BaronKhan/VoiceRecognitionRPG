package com.khan.baron.voicerecrpg;

import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.game.GameState;

import java.io.File;
import java.net.URL;

public class CallActivity extends AppCompatActivity {
    public VoiceControl mVoiceControl;

    public CallState mCallState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        mCallState = new CallState();
        mVoiceControl = new VoiceControl(this,
                (TextView) findViewById(R.id.txtInput),
                (TextView) findViewById(R.id.txtOutput),
                (TextView) findViewById(R.id.txtTimer),
                mCallState, false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.microphoneButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voice request in progress...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mVoiceControl.promptSpeechInput();
            }
        });

        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            Snackbar.make(findViewById(R.id.activity_call), "Found WordNet database",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
            try {
                URL url = new URL("file", null, dictFile.getPath());
                mCallState.addDictionary(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            Snackbar.make(findViewById(R.id.activity_call),
                    "Error: could not download WordNet database", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
