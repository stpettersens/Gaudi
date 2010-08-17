#!/bin/sh
# Build Gaudi on *nix systems

echo Compiling...
scalac -d bin -sourcepath /src/org/stpettersens/gaudi -verbose -deprecation -unchecked *.scala

echo Packaging...
cp Manifest.mf bin
cd bin
jar -cfm Gaudi.jar Manifest.mf *
rm -f Manifest.mf
mv Gaudi.jar ..
echo Done.

