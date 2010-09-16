#!/bin/bash
# Build Gaudi on *nix/Cygwin/MSYS

sep=";" # Initially use Windows, ";"

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use ":"
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	sep=":"
fi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"
deps="lib/json_simple-1.1.jar"$sep"lib/commons-io-1.4.jar"$sep"lib/groovy-all-1.7.4.jar"

echo "Compiling Gaudi..."
scalac -classpath $deps -verbose -deprecation -unchecked -d bin src/$pkg/*.scala

echo "Packaging Gaudi..."
cp Manifest.mf bin
cd bin
jar cfvm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."

