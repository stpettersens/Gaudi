#!/bin/bash
# Clear any log files 

files=( 
	*.log dist/*.log bin/*.log
)

echo "Clearing logs.."
for file in ${files[@]}
	do
		f=$file
		if [[ -e $f ]]; then
			echo Clearing $f
			rm -f -r $f
		fi
	done
echo "Done."
