#!/bin/bash
# Generate Gaudi executable for Windows
# using Launch4j with libs/ deps
# http://launch4j.sourceforge.net

if [[ -e "bin/Gaudi.jar" ]]; then
	cd dist
	rm *.log
	echo "Generating Windows executable (with deps).."
	launch4jc gaudi_exe.xml
	echo "Done."

else 
	echo "Important! Run 'sh/build.sh' first."
fi
