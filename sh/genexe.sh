#!/bin/bash
# Generate Gaudi executable for Windows
# using Launch4j with libs/ deps
# (installation version)
# http://launch4j.sourceforge.net

if [[ -e "bin/Gaudi.jar" ]]; then
	cd dist
	rm *.log
	echo "Generating Windows executable (with deps).."
	launch4jc gaudi_exe.xml
	echo "Built .exe *has* deps." >> built_exe.log
	echo "Done."

else echo "Important! You must run 'sh/build.sh' first."
fi
