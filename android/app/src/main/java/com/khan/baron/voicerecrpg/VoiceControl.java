package com.khan.baron.voicerecrpg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.khan.baron.voicerecrpg.system.GlobalState;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VoiceControl implements RecognitionListener {
    private final int PERMISSIONS_REQUEST_RECORD_AUDIO = 10;
    private final boolean DEBUG_TIMER = true;
    private final String LOG_TAG = "VoiceControl";

    private AppCompatActivity mActivity;

    private TextView mTxtInput;
    private TextView mTxtOutput;
    private TextView mTxtTimer;

    private SpeechRecognizer mSpeech;
    private Intent mRecognizerIntent;

    private GlobalState mState;

    private boolean mCanRecord;

    private boolean mAccumulateText;

    public VoiceControl(AppCompatActivity activity, TextView txtInput, TextView txtOutput,
                        TextView txtTimer, GlobalState state, boolean accumulateText) {
        super();
        mActivity = activity;
        mTxtInput = txtInput;
        mTxtOutput = txtOutput;
        mTxtTimer = txtTimer;
        mState = state;
        mAccumulateText = accumulateText;

        mTxtOutput.setMovementMethod(new ScrollingMovementMethod());

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
                mTxtInput.setText(mActivity.getText(R.string.permissions_enable));

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
        mTxtInput.setText(mActivity.getText(R.string.listening));
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

        mTxtInput.setText(text.toLowerCase());
        updateState(text);
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
                message = "Error from server";
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

    private void updateState(String input) {
        String newOutput = "\n\n----------\n\n\"" + mTxtInput.getText()+ "\"\n";
        long startTime = System.currentTimeMillis();
        String output = mState.updateState(input.toLowerCase());
        long endTime = System.currentTimeMillis();
        if (DEBUG_TIMER) {
            mTxtTimer.setText(getTimeAsString(endTime - startTime));
        } else {
            mTxtTimer.setText("");
        }
        newOutput += output;
        if (mAccumulateText) { appendOutputTextAndScroll(newOutput); }
        else { setOutputText(output); }
        // TODO: play TTS (should be optional)
    }

    //From: https://stackoverflow.com/a/625624/8919086
    private String getTimeAsString(long timeMillis) {
        return String.format("%02d:%02d:%03d",
                TimeUnit.MILLISECONDS.toMinutes(timeMillis),
                TimeUnit.MILLISECONDS.toSeconds(timeMillis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeMillis)),
                timeMillis % 1000
        );
    }

    public void setOutputText(String output) {
        mTxtOutput.setText(output);
    }

    // Taken from https://stackoverflow.com/a/9561614/8919086
    private void appendOutputTextAndScroll(String text) {
        if(mTxtOutput != null){
            mTxtOutput.append(text + "\n");
            final Layout layout = mTxtOutput.getLayout();
            if(layout != null){
                int scrollDelta = layout.getLineBottom(mTxtOutput.getLineCount() - 1)
                        - mTxtOutput.getScrollY() - mTxtOutput.getHeight();
                if(scrollDelta > 0)
                    mTxtOutput.scrollBy(0, scrollDelta);
            }
        }
    }

    public boolean canRecord() {
        return mCanRecord;
    }

    public void setCanRecord(boolean mCanRecord) {
        this.mCanRecord = mCanRecord;
    }
}
