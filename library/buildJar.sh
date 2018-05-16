#!/bin/bash

PKGNAME="com/khan/baron/vcw"

OUTPUTNAME="vcw.0.0.1.jar"
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
printf "cleaning up\n"
rm -Rf *.java *.class *.jar com

mkdir -p $PKGNAME

# copy source files
printf "copying source files\n"
cp $SOURCEDIR/*.java $PKGNAME/.

# remove android code
printf "removing android code\n"
for FILE in `ls $PKGNAME`
do
  if [ $FILE != "buildJar.sh" ]; then
    sed -i '/Log/d' ./$PKGNAME/$FILE
    sed -i '/Toast/d' ./$PKGNAME/$FILE
    sed -i '/MakeToast/d' ./$PKGNAME/$FILE
    sed -i '/candidates = "/d' ./$PKGNAME/$FILE
    sed -i '/new AssertionError());/d' ./$PKGNAME/$FILE
    sed -i '/+"= "+totalScore);/d' ./$PKGNAME/$FILE
    sed -i '/android.os.Environment;/d' ./$PKGNAME/$FILE
    sed -i '/android.app.Activity;/d' ./$PKGNAME/$FILE
    sed -i 's/android.util.Pair/com.khan.baron.vcw.Pair/g' ./$PKGNAME/$FILE
    sed -i 's/android.util.Triple/com.khan.baron.vcw.Triple/g' ./$PKGNAME/$FILE
    sed -i '/Environment.getExternalStorageDirectory()/d' ./$PKGNAME/$FILE
    sed -i 's/+ "\/english-left3words-distsim.tagger";/String modelPath="english-left3words-distsim.tagger";/g' ./$PKGNAME/$FILE
    sed -i 's/voicerecrpg\/system/vcw/g' ./$PKGNAME/$FILE
    sed -i 's/voicerecrpg.system/vcw/g' ./$PKGNAME/$FILE
  fi
done

printf "creating new classes\n"
printf "package com.khan.baron.vcw; public class Pair<F, S> { public final F first; public final S second; public Pair(F first, S second) { this.first=first; this.second=second; } }" > $PKGNAME/Pair.java
printf "package com.khan.baron.vcw; public class Triple<F, S, T> { public final F first; public final S second; public final T third; public Triple(F first, S second, T third) { this.first=first; this.second=second; this.third=third; } }" > $PKGNAME/Triple.java

printf "building jar\n"
javac -cp ".;$LIBCOMON;$LIBJWI;$LIBPOS;$LIBWS4J" $PKGNAME/*.java && jar cvf $OUTPUTNAME $PKGNAME/*.class

printf "created jar file: $OUTPUTNAME"

rm -Rf *.java *.class com
