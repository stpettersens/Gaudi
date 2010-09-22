#!/bin/bash
# Build Gaudi on *nix/Cygwin/MSYS

sep=";" # Initially use Windows, ";"

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use ":"
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	sep=":"
fi

pkg="org/stpettersens/gaudi"
jarF="Gaudi.jar"
groovyLib="lib/groovy-all-1.7.4.jar"
scalaLib="lib/scala-library.jar"
jsonLib="lib/json_simple-1.1.jar"
ioLib="lib/commons-io-1.4.jar"
deps="."$sep$jsonLib$sep$ioLib$sep$groovyLib

echo "Compiling Gaudi..."
scalac -classpath $deps src/$pkg/*.scala src/$pkg/*.java -verbose -deprecation -unchecked -d bin
javac -classpath $deps -verbose -deprecation -d bin src/$pkg/*.java

echo "Packaging Gaudi..."
cp Manifest.mf bin
cd bin
jar cfvm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."
