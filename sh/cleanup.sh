#!/bin/bash
# Clean-up files built for Gaudi
# Also clear up logs (see 'sh/clearlogs.sh')

sh/clearlogs.sh
sleep 2

files=( 
	dist/*.log dist/*.exe dist/*.jar dist/lib 
	bin/org bin/*.jar dist/*.class
)

echo "Cleaning up..."
for file in ${files[@]}
	do
		f=$file
		if [[ -e $f ]]; then
			echo Cleaning $f
			rm -f -r $f
		fi
	done
echo "Done."
