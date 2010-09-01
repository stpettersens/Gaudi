#!/bin/sh
# Build Gaudi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"

echo "Compiling..."
scalac -classpath ./lib -verbose -deprecation -unchecked -d bin src/$pkg/*.scala

echo "Packaging..."
cp Manifest.mf bin
cd bin
jar cfm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."
