#!/bin/bash
# Gather dependencies for building Gaudi 
# itself and related parts (e.g. instlaller) using GNU Wget
# http://www.gnu.org/software/wget/

# With no arguments, just gather dependencies needed to build Gaudi,
# either with rt dependencies or with embedded dependencies
# if necessary. Will be if Gaudi sources were just downloaded

buildPkgs=( 
	commons-io-1.4-bin.zip json_simple1.1.jar
	one-jar-boot-0.97 scala-library.7z
)
buildDeps=( 
	commons-io.1.4.jar json_simple1.1.jar 
	one-jar-boot-0.97.jar scala-library.jar
)
baseURL=( 
"http://mirrors.ukfast.co.uk/sites/ftp.apache.org///commons/io/binaries/"
"http://code.google.com/p/json-simple/downloads/detail?name="
)

function licenseWarn {
	echo
	echo "Warning! Downloading these dependencies confirms that"
	echo "you agree to use them in accordance with their respective"
	echo "licensing terms. Do you agree? (y/n)"
	read agree
	if [[ $agree =~  "n" ]]; then
		echo "You did not agree."
	elif [[ $agree =~ "y" ]]; then
		getBuildDeps
	fi
}
function getBuildDeps {
	i=$((i+0))
	for pkg in ${buildPkgs[@]}; do
		if [[ ! -e "lib/${buildDeps[$i]}" ]]; then
			echo "Downloading $pkg ($i/3 build deps)"
			cd lib && wget ${baseURL[$i]}$pkg
			echo "Done."
		else
			echo "Dep: ${buildDeps[$i]} was found."
		fi
		let i++
	done
	if [[ $buildPkg[$i] =~ *.[zip|7z] ]]; then
		cd lib && 7z x ${buildPkg[$i]}
	fi								
}
licenseWarn

