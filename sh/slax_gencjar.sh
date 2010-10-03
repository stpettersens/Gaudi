#!/bin/bash
# Generate Gaudi all-in-one JAR on Slax
# using One-JAR bootstrapper
# http://one-jar.sourceforge.net

wcard=.
appJar="Gaudi.jar"
oneJar="one-jar-boot-0.97.jar"
mainClass="One-Jar-Main-Class: org.stpettersens.gaudi.GaudiApp"

if [[ -e "bin/"$appJar ]]; then
	echo "Generating all-in-one JAR for Gaudi."
	echo "Structuring files for packaging..."
	mkdir cjar
	cp -r lib cjar
	cd cjar
	7z x lib/$oneJar 
	rm -f lib/$oneJar
	rm -f lib/.dummy
	rm OneJar.class
	rm -r -f src
	echo $mainClass >> boot-manifest.mf
	echo "" >> boot-manifest.mf
	mkdir main
	cd ..
	cp bin/$appJar cjar/main/$appJar

	echo "Packaging..."
	cd cjar
	jar cfvm c$appJar boot-manifest.mf $wcard
	cd ..
	mv cjar/c$appJar $appJar
	rm -f -r cjar
	echo "Done."
else
	echo "Important! Run 'sh/build.sh' first."
fi