#!/bin/bash
# Generate Gaudi all-in-one JAR
# using One-JAR bootstrap jar
# http://one-jar.sourceforge.net

wcard=* # Initially use Windows, "*"
appJar="Gaudi.jar"
oneJar="one-jar-boot-0.97.jar"
mainClass="One-Jar-Main-Class: org.stpettersens.gaudi.GaudiApp"

# For Linux, Unix/xBSD - use "."
if [[ `uname` =~ .*n.*x|.+BSD ]]; then
	wcard="."
fi

if [[ -e "bin/"$appJar ]]; then
	echo "**************************"
	echo "Generating all-in-one JAR."
	echo "**************************"
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
	cp bin/$appJar cjar/main

	echo "Packaging..."
	cd cjar
	jar cfvm c$appJar boot-manifest.mf $wcard
	cd ..
	mv cjar/c$appJar dist/$appJar
	rm -f -r cjar
	echo "Done."
else
	echo "Important! You must run 'sh/build.sh' first."
fi
