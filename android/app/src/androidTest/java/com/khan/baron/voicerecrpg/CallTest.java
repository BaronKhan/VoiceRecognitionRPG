package com.khan.baron.voicerecrpg;

import android.os.Environment;
import android.support.test.runner.AndroidJUnit4;

import com.khan.baron.voicerecrpg.call.CallState;
import com.khan.baron.voicerecrpg.game.GameState;
import com.khan.baron.voicerecrpg.game.enemies.Troll;
import com.khan.baron.voicerecrpg.game.items.Potion;
import com.khan.baron.voicerecrpg.game.items.Weapon;
import com.khan.baron.voicerecrpg.system.ContextActionMap;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class CallTest {
    private CallState callState;

    public CallTest() {
        callState = new CallState(null);
        ContextActionMap.setRememberUserSynonyms(false);
        File dictFile = new File(Environment.getExternalStorageDirectory().getPath()+"/dict/");
        if (dictFile.exists()) {
            System.out.println("Found WordNet database on phone");
            try {
                URL url = new URL("file", null, dictFile.getPath());
                callState.addDictionary(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Could not find WordNet database on phone");
        }
    }

    private void testCalled(String input, String contact, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains(contact), correctInput);
    }

    private void testCalledAudio(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("audio only"), correctInput);
    }

    private void testStoppedAllCalls(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("end all"), correctInput);
    }

    private void testStoppedCall(String input, String contact, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Ended call with "+contact), correctInput);
    }

    private void testMutedVideo(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Video muted"), correctInput);
    }

    private void testMutedAudio(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Audio muted"), correctInput);
    }

    private void testMutedContact(String input, String contact, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Muted "+contact), correctInput);
    }

    private void testUnmutedVideo(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Video unmuted"), correctInput);
    }

    private void testUnmutedAudio(String input, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Audio unmuted"), correctInput);
    }

    private void testUnmutedContact(String input, String contact, boolean correctInput) {
        String output = callState.updateState(input);
        assertEquals(output, output.contains("Unmuted "+contact), correctInput);
    }


    @Test
    public void testCallingSuite() {
        testCalled("call fred", "fred", true);
        testCalled("call jane", "jane", true);
        callState.endAllCalls();
        testCalled("phone jane", "jane", true);
        testCalled("contact fred", "fred", true);
        callState.endAllCalls();
        testCalled("call fred with video", "fred", true);
        testCalledAudio("use audio to phone jane", true);
        callState.endAllCalls();
        testCalled("call fred and jane", "jane", true);
    }

    @Test
    public void testStopCallsSuite() {
        callState.updateState("phone jane and fred");
        testStoppedAllCalls("stop all the calls", true);
        callState.updateState("yes and phone fred and jane");
        testStoppedAllCalls("end all of the conversations", true);
        callState.updateState("yes");
        callState.updateState("phone jane");
        testStoppedCall("stop call with jane", "jane", true);
        callState.updateState("phone jane");
        testStoppedCall("finish call with jane", "jane", true);
        callState.updateState("phone jane and fred");
        testStoppedAllCalls("hang up", true);
        callState.updateState("yes and phone fred");
        testStoppedCall("hang up with fred", "fred", true);
        callState.updateState("phone fred");
        testStoppedCall("stop calling fred", "fred", true);
    }

    @Test
    public void testMutedSuite() {
        callState.updateState("phone fred");
        testMutedVideo("mute my video", true);
        testMutedAudio("mute my audio", true);
        testUnmutedVideo("unmute my video", true);
        testUnmutedAudio("unmute my audio", true);
        testMutedAudio("silence my audio", true);
        testUnmutedAudio("increase my audio", true);
        testMutedContact("mute fred", "fred", true);
        testUnmutedContact("unmute fred", "fred", true);
    }

    @Test
    public void testDuplicates() {
        callState.updateState("phone baron");
        testCalled("first", "first", true);
        callState.updateState("phone baron");
        testCalled("second", "second", true);
    }
}
