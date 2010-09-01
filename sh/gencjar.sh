#!/bin/sh
# Generate Gaudi all-in-one JAR
# using One-JAR bootstrap jar
# http://one-jar.sourceforge.net

oneJar="one-jar-boot-0.97.jar"
mainClass="One-Jar-Main-Class: org.stpettersen.gaudi.GaudiApp\r\n"

echo "**************************"
echo "Generating all-in-one JAR."
echo "**************************"
echo "Structuring files for packaging..."
mkdir cjar
cp -r lib cjar
cd cjar
7z x lib/$oneJar 
rm -f $oneJar
rm OneJar.class
rm -r -f src
echo $mainClass >> boot-manifest.mf
echo "Done."
