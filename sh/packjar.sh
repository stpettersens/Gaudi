#!/bin/bash
# Just package Gaudi into a Jar

appJar="Gaudi.jar"

if [[ -e "bin/org/stpettersens/gaudi/GaudiApp.class" ]]; then
	echo "Packing JAR for Gaudi."
	cp Manifest.mf bin
	cd bin
	jar cvfm $appJar Manifest.mf org
	rm -f Manifest.mf
else
	echo "Important! Run 'sh/build.sh' first."
fi

