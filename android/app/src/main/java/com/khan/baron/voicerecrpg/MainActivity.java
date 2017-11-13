package com.khan.baron.voicerecrpg;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 10;

    private GameState mGameState;
    private VoiceControl mVoiceControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGameState = new GameState();

        mVoiceControl = new VoiceControl(this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            mVoiceControl.mCanRecord = (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    /// Reserved: may need to use C++ functions in the future
    public native String jniDummy();

    static {
        System.loadLibrary("native-lib");
    }
}
