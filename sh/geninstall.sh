#!/bin/sh
# Generate Gaudi executable installer for Windows
# using MakeNSIS with NSIS script
# http://nsis.sourceforge.net

echo "Generating Windows installer.."
makensis dist/gaudi_setup.nsi
echo "Done."
