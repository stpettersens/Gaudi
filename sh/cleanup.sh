#!/bin/bash
# Clean-up Gaudi generated files

files=( 
	dist/*.log dist/*.exe dist/*.jar bin/org bin/*.jar 
)

echo "Cleaning up..."
for file in ${files[@]}
	do
		f=$dir$file
		if [[ -e $f ]]; then
			echo Cleaning $f
			rm -f -r $f
		fi
done
echo "Done."

