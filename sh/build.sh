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
scSwitches="-verbose -deprecation -unchecked -d bin"

echo "Compiling Gaudi..."
scalac src/$pkg/GaudiLogger.scala src/$pkg/GaudiLogging.java $scSwitches
scalac src/$pkg/*.scala src/$pkg/*.java $scSwitches
javac -classpath $deps -verbose -deprecation -d bin src/$pkg/*.java
scalac -classpath $deps src/$pkg/*.scala src/$pkg/*.java $scSwitches

echo "Packaging Gaudi..."
cp Manifest.mf bin
cd bin
jar cfvm $jarF Manifest.mf *
rm Manifest.mf
cd ..
echo "Done."
