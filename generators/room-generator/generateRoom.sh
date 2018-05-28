#!/bin/bash
javac -cp ".;libs/ws4j-1.0.1.jar;libs/reverb-latest.jar" GenerateRoom.java
java -cp "libs/*;." GenerateRoom $1 $2
