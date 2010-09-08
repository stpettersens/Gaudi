#!/bin/bash
# Generate Gaudi executable installer for Windows
# using MakeNSIS with NSIS script
# http://nsis.sourceforge.net
#
# Also compile the small supporting applications
# for the installer. See 'sh/gensapps.sh'.

makensis=makensis # For Windows, use as-is

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use wine
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	makensis="wine $makensis"
fi

if [[ -e "dist/gaudi.exe" ]]; then
	echo "Generating Windows installer.."
	cd dist
	javac -deprecation -verbose -classpath . JavaCheck.java FindInPath.java
	$makensis gaudi_setup.nsi
	echo "Done."
else
	echo "Important! 'Run sh/genexe.sh' first."
fi
