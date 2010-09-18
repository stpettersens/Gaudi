#!/bin/bash
# Clean-up files built for Gaudi
# Also clear up logs (see 'sh/clearlogs.sh')

sh/clearlogs.sh
files=( 
	dist/*.log dist/*.exe dist/*.jar dist/lib 
	bin/org bin/*.jar dist/*.class dist/plugins
)

echo "Cleaning up..."
for file in ${files[@]}; do
	if [[ -e $file ]]; then
		echo Cleaning $file
		rm -f -r $file
	fi
done
echo "Done."
