import sys
import os.path
import csv

def renderBeginningFile(outputFile, outputName):
    outputFile.write("\
package com.khan.baron.voicerecrpg.game.rooms;\n\
/* TODO: insert object imports */\n\
\n\
public class "+outputName+" extends Room {\n\
    public "+outputName+"() {\n\
        super();\n"
    )

def renderEndFile(outputFile):
    outputFile.write("\
    }\n\
}"
    )

def renderJava(txtContents, outputName):
    outputFile = open(outputName+".java", 'w')

    renderBeginningFile(outputFile, outputName)

    lines = txtContents.split('.')
    for line in lines:
        lineTrimmed = line.strip()
        if lineTrimmed != "":
            outputFile.write("        addDescription(\""+line.strip()+".\");\n")

    renderEndFile(outputFile)

    outputFile.close()


if len(sys.argv) < 3:
    print("usage: python generateRoom.py <txt-path> <output-file-without-.java>")
    exit(0)

if ".java" in sys.argv[2]:
    print("error: remove .java extension from file name")
    exit(0)

fileName = sys.argv[1]
outputName = sys.argv[2]

if not os.path.isfile(fileName):
    print("error: "+fileName+" does not exist")
    exit(0)

# Code taken from https://stackoverflow.com/a/8369345/8919086
with open(fileName, 'r') as f:
    txtContents=f.read().replace("\n", " ")
    renderJava(txtContents, outputName)