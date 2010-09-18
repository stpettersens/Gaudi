#!/bin/bash
# Generate Gaudi executable for Windows
# using Launch4j with libs/ deps
# http://launch4j.sourceforge.net

l4j=launch4jc # Initially, use Windows launch4c console app.

# For Unix-likes (Linux/Unix/BSD/Darwin/Mac OS X),
# use the launch4j shell script
if [[ `uname` =~ .+n.+x|.+BSD|Darwin ]]; then
	l4j=launch4j
fi

if [[ -e "bin/Gaudi.jar" ]]; then
	echo "Generating Windows executable (with deps).."
	cp -r lib dist
	cp -r plugins dist
	cp bin/Gaudi.jar dist
	$l4j dist/gaudi_exe.xml
	echo "Done."

else 
	echo "Important! Run 'sh/build.sh' first."
fi
