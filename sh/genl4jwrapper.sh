#!/bin/bash
# Workaround for using Oracle/Sun JVM installed under wine
# which is required by Launch4j, but is not necessary
# installed natively under *nix. In my case, for instance,
# I am using Apache Harmony and its related JDK tools
# to build the Gaudi JAR, but the exe from Launch4j
# requires the de facto JVM. To solve this problem,
# run this shell script from project root.
#
# This will write out a shell script wrapper around
# a Windows Launch4j installation under wine where you
# have also installed the de facto JVM from Oracle/Sun.
#
if [[ ! -e /usr/bin/launch4j ]]; then
	wrapper=sh/launch4j
	echo "#!/bin/sh" >> $wrapper
	string="wine ~/.wine/drive_c/Program\ Files/Launch4j/launch4jc.exe %1"
	echo ${string//%/$} >> $wrapper
	echo "" >> $wrapper
	chmod +x $wrapper
	sudo mv $wrapper /usr/bin
		echo "Wrote *nix wrapper for launch4jc.exe"
	else
		echo "launch4jc.exe wrapper already exists!"
fi

