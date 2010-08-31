#!/bin/sh
# Generate Gaudi executable for Windows
# using Launch4j(c)
# http://launch4j.sourceforge.net

echo "Generating Windows executable.."
launch4jc gaudi_exe.xml
echo "Done."
