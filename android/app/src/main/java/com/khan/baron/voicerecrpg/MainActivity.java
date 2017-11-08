package com.khan.baron.voicerecrpg;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private GameState mGameState;
    private VoiceControl mVoiceControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameState = new GameState();

        mVoiceControl = new VoiceControl(
                (TextView) findViewById(R.id.txtInput),
                (TextView) findViewById(R.id.txtOutput),
                mGameState);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.microphoneButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Voice request in progress...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                mVoiceControl.promptSpeechInput();
            }
        });
    }

    /// Reserved: may need to use C++ functions in the future
    public native String jniDummy();

    static {
        System.loadLibrary("native-lib");
    }
}
