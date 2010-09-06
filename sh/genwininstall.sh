#!/bin/bash
# Generate Gaudi executable installer for Windows
# using MakeNSIS with NSIS script
# http://nsis.sourceforge.net
#
# Also compile the small Java version check assisting
# program used by the installer with a Java compiler
# Uses javac by default, but gcj should work too.
# http://java.sun.com
# http://gcc.gnu.org/java

makensis=makensis # For Windows, use as-is
javac=javac

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use wine
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	makensis="wine $makensis"
fi

if [[ -e "dist/gaudi.exe" ]]; then
	echo "Generating Windows installer.."
	cd dist
	$javac -verbose -classpath . JavaCheck.java
	$makensis gaudi_setup.nsi
	echo "Done."
else
	echo "Important! 'Run sh/genexe.sh' first."
fi
