#!/bin/bash
# Clear any log files 

files=( 
	*.log bin/*.log
)

echo "Clearing logs.."
for file in ${files[@]}
	do
		if [[ -e $file ]]; then
			echo Clearing $file
			rm -f -r $file
		fi
	done
echo "Done."
