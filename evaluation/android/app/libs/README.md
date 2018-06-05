# YOU MUST ADD THE FOLLOWING JAR FILES

- commons-compress 1.15 (https://jar-download.com/explore-java-source-code.php?a=commons-compress&g=org.apache.commons&v=1.15&downloadable=1)
- edu.mit.jwi_2.4.0 (https://projects.csail.mit.edu/jwi/)
- stanford-postagger (https://nlp.stanford.edu/software/tagger.shtml#Download)
- ws4j-1.0.1 (https://code.google.com/archive/p/ws4j/downloads)
- vcw.0.0.1 (https://github.com/BaronKhan/voice-commands-with-wordnet)

# Changes required for the WS4J library

In order to build the Android project with the WS4J library as a dependency,
you must extract the archive (any unzipping program will work) and remove the
`NictWordNet.class` file from it, then zip again into an archive file.