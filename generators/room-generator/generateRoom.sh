#!/bin/bash

LIBWS4J="libs/ws4j-1.0.1.jar"
LIBREVERB="libs/reverb-latest.jar"

javac -cp ".;$LIBWS4J;$LIBREVERB" GenerateRoom.java
java -cp "libs/*;." GenerateRoom $1 $2
