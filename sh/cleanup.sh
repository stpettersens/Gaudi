#!/bin/bash
# Clean-up files built for Gaudi
# Also clear up logs (see 'sh/clearlogs.sh')

sh/clearlogs.sh
files=( 
	*.exe *.jar bin/org
	bin/*.jar *.class
)

echo "Cleaning up..."
for file in ${files[@]}; do
	if [[ -e $file ]]; then
		echo Cleaning $file
		rm -r -f $file
	fi
done
echo "Done."
