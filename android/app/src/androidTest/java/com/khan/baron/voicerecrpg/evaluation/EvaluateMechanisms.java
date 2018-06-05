package com.khan.baron.voicerecrpg.evaluation;

import android.util.Log;

public class EvaluateMechanisms {
    private static int score = 0;
    private static int maxScore = 0;

    public static void runTest(String message, boolean arg1, boolean arg2) {
        if (arg1 == arg2) {
            score++;
            Log.d("EvaluateMechanisms", "passed");
        } else {
            Log.d("EvaluateMechanisms", "failed: "+message);
        }
        maxScore++;
        Log.d("EvaluateMechanisms", "score = "+score+"/"+maxScore);
    }

    public static void runTest(boolean arg1, boolean arg2) {
        runTest("", arg1, arg2);
    }
}
