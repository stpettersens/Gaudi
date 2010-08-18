#!/bin/sh
# Build Gaudi on *nix systems

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"

echo "Compiling..."
scalac -verbose -deprecation -unchecked -d bin src/$pkg/*.scala

echo "Packaging..."
cp Manifest.mf bin
cd bin
jar cfm $jarF Manifest.mf *
mv $jarF ..
rm Manifest.mf
cd ..
echo "Done."
