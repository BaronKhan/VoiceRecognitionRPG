#!/bin/bash

SOURCEDIR="../android/app/src/main/java/com/khan/baron/voicerecrpg/system"
LIBDIR="../android/app/libs"
LIBCOMMON="$LIBDIR/commons-compress-1.15.jar"
LIBJWI="$LIBDIR/edu.mit.jwi_2.4.0.jar"
LIBPOS="$LIBDIR/stanford-postagger.jar"
LIBWS4J="$LIBDIR/ws4j-1.0.1.jar"

if [ ! -d "../android" ]; then
  echo "execute this script in the library/ dir"
  exit 0
fi

# clean up
rm -Rf *.java *.class *.jar

# copy source files
cp $SOURCEDIR/*.java .

# remove android code
for FILE in `ls .`
do
  if [ $FILE != "buildJar.sh" ]; then
    sed -i '/Log/d' ./$FILE
    sed -i '/Toast/d' ./$FILE
    sed -i '/MakeToast/d' ./$FILE
    sed -i '/candidates = "/d' ./$FILE
    sed -i '/new AssertionError());/d' ./$FILE
    sed -i '/+"= "+totalScore);/d' ./$FILE
    sed -i '/android.os.Environment;/d' ./$FILE
    sed -i '/android.app.Activity;/d' ./$FILE
    sed -i 's/android.util.Pair/com.khan.baron.vcw.Pair/g' ./$FILE
    sed -i 's/android.util.Triple/com.khan.baron.vcw.Triple/g' ./$FILE
    sed -i '/Environment.getExternalStorageDirectory()/d' ./$FILE
    sed -i 's/+ "\/english-left3words-distsim.tagger";/String modelPath="english-left3words-distsim.tagger";/g' ./$FILE
    sed -i 's/voicerecrpg\/system/vcw/g' ./$FILE
    sed -i 's/voicerecrpg.system/vcw/g' ./$FILE
  fi
done

printf "package com.khan.baron.vcw; public class Pair<F, S> { public final F first; public final S second; public Pair(F first, S second) { this.first=first; this.second=second; } }" > Pair.java
printf "package com.khan.baron.vcw; public class Triple<F, S, T> { public final F first; public final S second; public final T third; public Triple(F first, S second, T third) { this.first=first; this.second=second; this.third=third; } }" > Triple.java

javac -cp ".;$LIBCOMON;$LIBJWI;$LIBPOS;$LIBWS4J" *.java && jar cvf vcw.0.0.1.jar *.class

rm -Rf *.java *.class
