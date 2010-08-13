#!/bin/sh
# Build Gaudi on *nix systems

echo Compiling...
cd src
scalac -d ../bin -deprecation -unchecked *.scala

echo Packaging...
cd ..
cp Manifest.mf bin
cd bin
jar -cfm Gaudi.jar Manifest.mf *
rm -f Manifest.mf
mv Gaudi.jar ..
echo Done.

