#!/bin/bash
javac -cp ".;lib/ws4j-1.0.1.jar" *.java
java -cp "lib/*;." PropertyChecker $1 $2
