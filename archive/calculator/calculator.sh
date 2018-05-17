#!/bin/bash
javac -cp ".;lib/ws4j-1.0.1.jar" *.java
java -cp "lib/*;." SimilarityCalculationDemo $1 $2
