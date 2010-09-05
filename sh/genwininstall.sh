#!/bin/bash
# Generate Gaudi executable installer for Windows
# using MakeNSIS with NSIS script
# http://nsis.sourceforge.net

makensis=makensis # For Windows, use as-is

# For Unix-likes (Linux/Unix/xBSD/Darwin/Mac OS X) - use wine
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
     makensis="wine $makensis"
fi

if [[ -e "dist/gaudi.exe" ]]; then
	echo "Generating Windows installer.."
	$makensis dist/gaudi_setup.nsi
	echo "Done."
else
	echo "Important! 'Run sh/genexe.sh' first."
fi

