import java.io.*;
import java.util.*;
import java.text.*;

public class GenerateRoom {
  public static void main(String args[]) throws IOException {
    if (args.length < 2) {
      System.out.println("usage: python generateRoom.py <txt-path>"+
        " <output-file-without-.java>");
      System.exit(0);
    }

    if (args[1].contains(".java")) {
      System.out.println("error: remove .java extension from file name");
      System.exit(0);
    }

    String txtFileName = args[0];
    String outputName = args[1];

    File file = new File(txtFileName);
    if (!file.exists()) {
      System.out.println("error: "+txtFileName+" does not exist");
      System.exit(0);
    }

    renderJava(txtFileName, outputName);
  }

  public static void renderJava(String txtFileName, String outputName)
    throws IOException
  {
    BufferedWriter writer = new BufferedWriter(new FileWriter(outputName+".java"));

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
    writer.write("        addDescription(\n                \""+
      sentence+"\");\n");
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