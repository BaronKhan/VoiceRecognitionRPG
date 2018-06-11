import java.io.*;
import java.util.*;
import java.text.*;
import java.nio.file.*;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

import edu.washington.cs.knowitall.nlp.ChunkedSentence;
import edu.washington.cs.knowitall.nlp.OpenNlpSentenceChunker;
import edu.washington.cs.knowitall.extractor.ReVerbExtractor;
import edu.washington.cs.knowitall.extractor.conf.ConfidenceFunction;
import edu.washington.cs.knowitall.extractor.conf.ReVerbOpenNlpConfFunction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedBinaryExtraction;
import edu.washington.cs.knowitall.nlp.extraction.ChunkedArgumentExtraction;

public class GenerateRoom {
    private static ILexicalDatabase sDb = new NictWordNet();
    private static RelatednessCalculator sCalc = new WuPalmer(sDb);
    public static Map<String, String> sObjectMap = new HashMap();
    public static List<String> sCreatedObjects = new ArrayList();
    public static ChunkedSentence sChunkedText;
    private static ReVerbExtractor reverb = new ReVerbExtractor();

    private static final double THRESHOLD = 0.8;

    public static void main(String args[]) throws IOException {
        WS4JConfiguration.getInstance().setMFS(false);

        if (args.length < 3) {
            System.out.println("usage: python generateRoom.py <txt-path>"+
                " <object-list> <output-file-without-.java>");
            System.exit(0);
        }

        if (args[2].contains(".java")) {
            System.out.println("error: remove .java extension from file name");
            System.exit(0);
        }

        String txtFileName = args[0];
        String objFileName = args[1];
        String outputName = args[2];

        File file = new File(txtFileName);
        if (!file.exists()) {
            System.out.println("error: "+txtFileName+" does not exist");
            System.exit(0);
        }

        file = new File(objFileName);
        if (!file.exists()) {
            System.out.println("error: "+objFileName+" does not exist");
            System.exit(0);
        }

        System.out.println("processing object file");
        processObjects(objFileName);

        System.out.println("rendering java file");
        renderJava(txtFileName, outputName);

        System.out.println("sucessfully rendered "+outputName+".java");
    }

    public static void processObjects(String objFileName) throws IOException {
        Scanner input = new Scanner(new File(objFileName));
        while (input.hasNextLine()) {
            String line = input.nextLine();
            String[] parts = line.split("\\|");
            sObjectMap.put(parts[0], parts[1]);
        }
    }

    public static void renderJava(String txtFileName, String outputName)
        throws IOException
    {
        BufferedWriter writer = 
            new BufferedWriter(new FileWriter(outputName+".java"));

        renderBeginning(writer, outputName);

        generateRoom(writer, txtFileName);

        renderEnd(writer);

        writer.close();
    }

    public static void renderBeginning(BufferedWriter writer, String outputName)
        throws IOException
    {
        writer.write(
            "package com.khan.baron.voicerecrpg.game.rooms;\n"+
            "/* TODO: insert object imports */\n\n"+
            "public class "+outputName+" extends Room {\n"+
            "    public "+outputName+"() {\n"+
            "        super();\n"
        );
    }

    public static void generateRoom(BufferedWriter writer, String txtFileName)
        throws FileNotFoundException, IOException
    {
        //abhinandanmk.blogspot.com/2012/05/java-how-to-read-complete-text-file.html
        String entireFileText = new Scanner(new File(txtFileName))
            .useDelimiter("\\A").next();

        System.out.println("extracting binary relationships");
        sChunkedText = (new OpenNlpSentenceChunker())
            .chunkSentence(entireFileText);

        //Better sentence-breaking: https://stackoverflow.com/a/2687929/8919086
        BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.UK);
        String source = entireFileText;
        iterator.setText(source);
        int start = iterator.first();
        for (int end = iterator.next();
                end != BreakIterator.DONE;
                start = end, end = iterator.next())
        {
            String sentence = source.substring(start,end).trim();
            processSentence(writer, sentence);
        }
    }

    public static void processSentence(BufferedWriter writer, String sentence)
        throws IOException
    {
        //First phase: find binary relationships
        boolean foundRel = false;
        String relCond = "";
        String altSentence = "";
        for (ChunkedBinaryExtraction extr : reverb.extract(sChunkedText)) {
            String arg1 = extr.getArgument1().toString();
            String rel = extr.getRelation().toString();
            String arg2 = extr.getArgument2().toString();
            if (sentence.contains(arg1) && sentence.contains(arg2)) {
                //If one of the relationships is a valid object (no *), then
                //add with conditional
                for (String objWord : sObjectMap.keySet()) {
                    if ((!arg1.contains("*")) && (arg1.contains(objWord)) 
                        || ((!arg2.contains("*")) && (arg2.contains(objWord))))
                    {
                        relCond = "() -> getRoomObjectCount(\""+objWord+"\") > 0";
                        arg1 = arg1.replaceAll("\\*", "");
                        if (rel.contains("on")) {
                            altSentence = "\""+arg1.substring(0, 1).toUpperCase()
                                +arg1.substring(1)+" is now on the floor.\"";
                        } else {
                            altSentence = "\""+arg1.substring(0, 1).toUpperCase()
                                +arg1.substring(1)+" is in the room.\"";
                        }
                        foundRel = true;
                        break;
                    }
                }
                if (foundRel) { break; }
            }
        }
        
        //Second phase: search for objects
        String[] words = sentence.replaceAll("[^a-zA-Z* ]", "").toLowerCase()
                            .split("\\s+");
        String prettySentence = sentence.replaceAll("\\*", "");
        for (String word : words) {
            if (!word.contains("*")) { continue; }
            word = word.replaceAll("\\*","");
            if (sObjectMap.keySet().contains(word)) {
                if (foundRel) {
                    writer.write("        addDescriptionWithObjectCond(\n            \""+
                        prettySentence+"\",\n            "+
                        altSentence+",\n            new "+
                        sObjectMap.get(word)+",\n            "+
                        relCond+");\n");
                } else {
                    writer.write("        addDescriptionWithObject(\n            \""+
                        prettySentence+"\",\n            new "+
                        sObjectMap.get(word)+");\n");
                }
                return;
            }
            //Search using semantic similarity
            double bestScore = 0.0;
            String bestObj = null;
            for (String objWord : sObjectMap.keySet()) {
                double score = Math.min(sCalc.calcRelatednessOfWords(word, objWord),
                                    1.0);
                if (score > bestScore) {
                    bestScore = score;
                    bestObj = objWord;
                }
            }
            if (bestScore > THRESHOLD) {
                if (foundRel) {
                    writer.write("        addDescriptionWithObjectCond(\n            \""+
                        prettySentence+"\",\n            "+
                        altSentence+",\n            new "+
                        sObjectMap.get(bestObj)+",\n            "+
                        relCond+");\n");
                } 
                else {
                    writer.write("        addDescriptionWithObject(\n            \""+
                        prettySentence+"\",\n            new "+
                        sObjectMap.get(bestObj)+");\n");
                }
                return;
            }
        }

        //Third phase: just add sentence
        if (foundRel) {
            writer.write("        addDescription(\n            \""+
                prettySentence+"\",\n            "+
                relCond+");\n");
        } else {
            writer.write("        addDescription(\n            \""+
                prettySentence+"\");\n");
        }
    }

    public static void renderEnd(BufferedWriter writer)
        throws IOException
    {
        writer.write(
            "    }\n"+
            "}"
        );
    }
}