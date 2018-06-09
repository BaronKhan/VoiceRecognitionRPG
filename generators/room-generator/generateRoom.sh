#!/bin/bash

LIBWS4J="libs/ws4j-1.0.1.jar"
LIBREVERB="libs/reverb-latest.jar"

printf "building program\n"
javac -cp ".;$LIBWS4J;$LIBREVERB" GenerateRoom.java
printf "running program\n"
java -cp "libs/*;." GenerateRoom $1 $2 $3
