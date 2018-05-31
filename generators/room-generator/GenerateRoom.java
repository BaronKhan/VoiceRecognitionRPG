import java.io.*;
import java.util.*;
import java.text.*;
import java.nio.file.*;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class GenerateRoom {
    private static ILexicalDatabase sDb = new NictWordNet();
    private static RelatednessCalculator sCalc = new WuPalmer(sDb);
    public static Map<String, String> sObjectMap = new HashMap();

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

        processObjects(objFileName);

        renderJava(txtFileName, outputName);
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
        //Second phase: search for objects
        String[] words = sentence.replaceAll("[^a-zA-Z* ]", "").toLowerCase()
                            .split("\\s+");
        for (String word : words) {
            if (!word.contains("*")) { continue; }
            word = word.replaceAll("\\*","");
            if (sObjectMap.keySet().contains(word)) {
                writer.write("        addDescriptionWithObject(\n            \""+
                    sentence.replaceAll("\\*", "")+"\",\n            new "+
                    sObjectMap.get(word)+");\n");
                return;
            }
        }
        //Third phase: just add sentence
        writer.write("        addDescription(\n            \""+
            sentence.replaceAll("[\\*]", "")+"\");\n");
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