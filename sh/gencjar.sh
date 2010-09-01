#!/bin/sh
# Generate Gaudi all-in-one JAR
# using One-JAR bootstrap jar
# http://one-jar.sourceforge.net

appJar="Gaudi.jar"
oneJar="one-jar-boot-0.97.jar"
mainClass="One-Jar-Main-Class: org.stpettersens.gaudi.GaudiApp\r\n"

echo "**************************"
echo "Generating all-in-one JAR."
echo "**************************"
echo "Structuring files for packaging..."
mkdir cjar
cp -r lib cjar
cd cjar
7z x lib/$oneJar 
rm -f lib/$oneJar
rm -f lib/.dummy
rm OneJar.class
rm -r -f src
echo $mainClass >> boot-manifest.mf
mkdir main
cd ..
cp bin/$appJar cjar/main

echo "Packaging..."
cd cjar
jar cfvm $appJar boot-manifest.mf .
cd ..
mv cjar/$appJar dist
rm -f -r cjar
echo "Done."
