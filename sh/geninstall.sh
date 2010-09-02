#!/bin/bash
# Generate Gaudi executable installer for Windows
# using MakeNSIS with NSIS script
# http://nsis.sourceforge.net

if [[ -e "dist/gaudi.exe" ]]; then
	echo "Generating Windows installer.."
	makensis dist/gaudi_setup.nsi
	echo "Done."
else
	echo "Important! 'Run sh/genexe.sh' first."
fi
