package com.khan.baron.voicerecrpg.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import edu.stanford.nlp.util.Triple;

import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class SentenceMapper {
    private ContextActionMap mContextActionMap;
    private List<Triple<Action, String, List<String>>> mSentenceList = new CopyOnWriteArrayList<>();

    private final double THRESHOLD = 0.5;

    private static final boolean USING_PAR = false;

    public SentenceMapper(ContextActionMap contextActionMap) { mContextActionMap = contextActionMap; }

    public void addSentenceMatch(Action action, String targetName, String ... examples) {
        mSentenceList.add(new Triple<>(action, targetName,
                new CopyOnWriteArrayList<>(Arrays.asList(examples))));
    }

    public Triple<Action, String, List<String>> checkSentenceMatch(List<String> words) {
        Triple<Action, String, List<String>> bestTriple = null;
        double bestScore = THRESHOLD;
        for (Triple<Action, String, List<String>> triple : mSentenceList) {
            List<String> sentences = triple.third;
            double totalScore = 0.0;
            if (sentences.size() > 0) {
                if (USING_PAR) {
                    try {
                        List<String> sentencesCopy = new CopyOnWriteArrayList<>(sentences);
                        Log.d("SentenceMapper", "Running parallel stream...");
                        totalScore = sentencesCopy.parallelStream().unordered()
                                .mapToDouble((sentence) -> calculateCosScore(words, sentence))
                                .sum();
                        Log.d("SentenceMapper", "Finished parallel stream...");
                    } catch (RuntimeException e) {
                        Log.d("SentenceMapper", "Error while running parallel stream. Running sequential version...");
                        totalScore = 0.0;
                        for (String sentence : sentences) {
                            totalScore += calculateCosScore(words, sentence);
                        }
                    }
                } else {
                    for (String sentence : sentences) { totalScore += calculateCosScore(words, sentence); }
                }
                totalScore /= (double) sentences.size();
            }
            totalScore = min(totalScore, 1.0);
            Log.d("SentenceMapper", "Score for "+words.toString()+" --> "+sentences.toString()+"\n"
                    +"= "+totalScore);
            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestTriple = triple;
            }
        }
        if (bestScore > THRESHOLD) { return bestTriple; }
        else { return null; }
    }

    public double calculateCosScore(List<String> words1, String sentence) {
        try {
            Map<String, Integer> vector1 = getVector(words1);
            Map<String, Integer> vector2 = getVector(sentence);

            //Get common keys
            double numerator = 0.0;
            for (Map.Entry<String, Integer> entry1 : vector1.entrySet()) {
                for (Map.Entry<String, Integer> entry2 : vector2.entrySet()) {
                    if (entry1.getKey().equals(entry2.getKey())) {
                        numerator += entry1.getValue() * entry2.getValue();
                    } else {
                        numerator += 0.25 * min(SemanticSimilarity.getInstance().calculateScore(
                                entry1.getKey(), entry2.getKey()) *
                                (entry1.getValue() * entry2.getValue()), 1.0);
                    }
                }
            }

            double sum1 = 0.0;
            for (Map.Entry<String, Integer> entry1 : vector1.entrySet()) {
                sum1 += pow(entry1.getValue(), 2);
            }

            double sum2 = 0.0;
            for (Map.Entry<String, Integer> entry1 : vector2.entrySet()) {
                sum2 += pow(entry1.getValue(), 2);
            }

            double denominator = sqrt(sum1) + sqrt(sum2);

            if (denominator <= 0.0) {
                return 0.0;
            } else {
                return numerator / denominator;
            }
        } catch (Exception e) {
//            Log.e("SentenceMapper", "Error occurred in CalculateCosScore: "+e.getMessage());
            return 0.0;
        }
    }

    private Map<String, Integer> getVector(String input) {
        List<String> words = new ArrayList<>(Arrays.asList(input.toLowerCase().split(" ")));
        return getVector(words);
    }

    private Map<String, Integer> getVector(List<String> words) {
        Map<String, Integer> vector = new ConcurrentHashMap<>();
        for (String word : words) {
            if (vector.containsKey(word)) {
                vector.put(word, vector.get(word)+1);
            } else {
                vector.put(word, 1);
            }
        }
        return vector;
    }
}
