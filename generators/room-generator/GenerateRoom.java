import java.io.*;
import java.util.*;


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

    String fileName = args[0];
    String outputName = args[1];

    File file = new File(fileName);
    if (!file.exists()) {
      System.out.println("error: "+fileName+" does not exist");
      System.exit(0);
    }

    renderJava(outputName);
  }

  public static void renderJava(String outputName) throws IOException {
    BufferedWriter writer = new BufferedWriter(new FileWriter(outputName+".java"));

    renderBeginning(writer, outputName);

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

  public static void renderEnd(BufferedWriter writer)
    throws IOException
  {
    writer.write(
      "    }\n"+
      "}"
    );
  }
}