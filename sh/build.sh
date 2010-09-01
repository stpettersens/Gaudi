#!/bin/bash
# Build Gaudi

sep=";" # initially use Windows, ";"

# For Linux, Unix/xBSD - use ":"
if [[ `uname` =~ .*n.*x|.+BSD ]]; then
	sep=":"
fi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"
deps="lib/json_simple-1.1.jar"$sep"lib/commons-io-1.4.jar"

echo "Compiling..."
scalac -classpath $deps -verbose -deprecation -unchecked -d bin src/$pkg/*.scala

echo "Packaging..."
cp Manifest.mf bin
cd bin
jar cfm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."
