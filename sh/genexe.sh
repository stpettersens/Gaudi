#!/bin/bash
# Generate Gaudi executable for Windows
# using Launch4j with libs/ deps
# http://launch4j.sourceforge.net

if [[ -e "bin/Gaudi.jar" ]]; then
	echo "Generating Windows executable (with deps).."
	cp -r lib dist
	cp bin/Gaudi.jar dist
	launch4jc dist/gaudi_exe.xml
	rm -f dist/Gaudi.jar
	echo "Done."

else 
	echo "Important! Run 'sh/build.sh' first."
fi
