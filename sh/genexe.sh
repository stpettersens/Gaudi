#!/bin/sh
# Generate Gaudi executable for Windows
# using Launch4j(c)
# http://launch4j.sourceforge.net

echo "Generating Windows executable.."
cp -r lib dist/
cp bin/Gaudi.jar dist/
launch4jc dist/gaudi_exe.xml
echo "Done."
