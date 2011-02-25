#!/bin/bash
# Build Gaudi on *nix/Cygwin/MSYS

sep=";" # Initially use Windows, ";"

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use ":"
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	sep=":"
fi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"
groovyLib="lib/groovy-all-1.7.5.jar"
scalaLib="lib/scala-library.jar"
jsonLib="lib/json_simple-1.1.jar"
ioLib="lib/commons-io-2.0.jar"
deps="."$sep$scalaLib$sep$jsonLib$sep$ioLib$sep$groovyLib
switches="-verbose -deprecation -d bin"

echo "Compiling Gaudi..."
scalac -classpath $deps $switches src/$pkg/*
javac -classpath $deps $switches src/$pkg/*.java

echo "Packaging Gaudi..."
cp Manifest.mf bin
cd bin
jar cfvm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."

