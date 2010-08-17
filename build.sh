#!/bin/sh
# Build Gaudi on *nix systems
# 
# Notes:
# Problem with -sourcepath switch for scalac?

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"

echo "Compiling..."
cd src/$pkg
scalac -deprecation -unchecked *.scala

echo "Copying over classes..."
cp -r org ../../../../bin
rm -r -f org

echo "Packaging..."
cd ../../../../
cp Manifest.mf bin
cd bin
jar cfm $jarF Manifest.mf *
rm Manifest.mf
mv $jarF ..
echo "Done."
