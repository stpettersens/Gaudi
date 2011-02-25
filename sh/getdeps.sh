#!/bin/bash
# Gather dependencies for building Gaudi 
# itself and related parts (e.g. instlaller) using GNU Wget
# http://www.gnu.org/software/wget/

# With no arguments, just gather dependencies needed to build Gaudi,
# either with rt dependencies or with embedded dependencies
# if necessary. Will be if Gaudi sources were just downloaded
buildDeps=( 
	commons-io-1.4.jar json_simple-1.1.jar 
	scala-library.jar
)
buildURL=(
 "ftp://mirrors.dedipower.com/ftp.apache.org//commons/io/binaries/"
 "http://code.google.com/p/json-simple/downloads/detail?name="
 "null"
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
	reset
	i=$((i+0))
	for dep in ${buildDeps[@]}; do
		if [[ ! -e "lib/$dep" ]]; then
			echo "Downloading $dep ($((i+1))/3 build deps)"
			if [[ $dep == "commons-io-1.4.jar" ]]; then
				dep="commons-io-1.4-bin.zip"
			fi
			cd lib && wget ${buildURL[$i]}$dep
			if [[ $dep == "commons-io-1.4-bin.zip" ]]; then
				7z x $dep
				rm -f $dep
				cd commons-io-1.4 
				mv commons-io-1.4.jar ..
				cd ..
				rm -f -r commons-io-1.4
			fi
			if [[ $dep == "scala-library.jar" ]]; then
				scala_dir=`whereis scala`
				echo $scala_dir
			fi
			echo "Done."
		else
			echo "Dep: ${buildDeps[$i]} was found."
		fi
		let i++
	done
}
licenseWarn

