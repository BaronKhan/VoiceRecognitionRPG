package com.khan.baron.voicerecrpg;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

public class VoiceControl implements RecognitionListener {
    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    private final String LOG_TAG = "VoiceControl";

    private AppCompatActivity mActivity;

    private TextView mTxtInput;
    private TextView mTxtOutput;

    private SpeechRecognizer mSpeech = null;
    private Intent mRecognizerIntent;

    private GameState mGameState;

    public boolean mCanRecord;

    public VoiceControl(AppCompatActivity activity, TextView txtInput, TextView txtOutput, GameState gameState) {
        super();
        mActivity = activity;
        mTxtInput = txtInput;
        mTxtOutput = txtOutput;
        mGameState = gameState;

        mCanRecord = false;
        checkRecordAudioPermission();

        mSpeech = SpeechRecognizer.createSpeechRecognizer(mActivity);
        mSpeech.setRecognitionListener(this);
        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    public void checkRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(mActivity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity,
                    Manifest.permission.RECORD_AUDIO)) {
                mTxtInput.setText("Please enable permissions");

            } else {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }

    public void promptSpeechInput() {
        Log.i(LOG_TAG, "promptSpeechInput");
        if (!mCanRecord) {
            checkRecordAudioPermission();
        }
        mTxtInput.setText("Listening...");
        mSpeech.startListening(mRecognizerIntent);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(LOG_TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onBufferReceived(byte[] buffer) {}

    @Override
    public void onEndOfSpeech() {
        Log.i(LOG_TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);
        mTxtInput.setText(errorMessage);
    }

    @Override
    public void onEvent(int arg0, Bundle arg1) {
        Log.i(LOG_TAG, "onEvent");
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(LOG_TAG, "onPartialResults");
        ArrayList<String> matches = partialResults
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        if (matches.size() > 0) {
            mTxtInput.setText(matches.get(0));
        }
    }

    @Override
    public void onReadyForSpeech(Bundle arg0) {
        Log.i(LOG_TAG, "onReadyForSpeech");
    }

    @Override
    public void onResults(Bundle results) {
        Log.i(LOG_TAG, "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        String text = "";

        // TODO: we may have multiple matches for the phrase, try to infer which one is correct
        // For now just get first match
        if (matches.size() > 0) {
            text += matches.get(0);
        }

        mTxtInput.setText(text);
        updateGameState(text);
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i(LOG_TAG, "onRmsChanged: " + rmsdB);
    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }

    private void updateGameState(String input) {
        mTxtOutput.setText((mGameState.updateState(input)));

        // TODO: play TTS (should be optional)
    }

}
