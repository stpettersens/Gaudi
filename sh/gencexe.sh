#!/bin/bash
# Generate Gaudi executable for Windows
# using Launch4j(c) without libs/ deps
# (non-installation version)
# http://launch4j.sourceforge.net

if [[ -e "dist/Gaudi.jar" ]]; then
	cd dist
	rm *.log
	echo "Generating Windows executable (no deps).."
	launch4jc gaudi_exe_nocp.xml
	echo "Built .exe has no deps." >> built_exe.log
	echo "Done."

else 
	echo "Important! You must run 'sh/gencjar.sh' and 'sh/build.sh' first."
fi
