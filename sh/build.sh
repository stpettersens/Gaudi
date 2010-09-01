#!/bin/sh
# Build Gaudi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"
deps="lib/json_simple-1.1.jar;lib/commons-io-1.4.jar"

echo "Compiling..."
scalac -classpath $deps -verbose -deprecation -unchecked -d bin src/$pkg/*.scala

echo "Packaging..."
cp Manifest.mf bin
cd bin
jar cfm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."
