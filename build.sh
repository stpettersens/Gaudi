#!/bin/sh
# Build Gaudi on *nix systems
cp Manifest.mf bin
cd bin
jar -cfm Gaudi.jar Manifest.mf *
rm -f Manifest.mf
mv Gaudi.jar ..
