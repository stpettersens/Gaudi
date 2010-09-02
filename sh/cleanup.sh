#!/bin/sh
# Clean-up Gaudi generated files

echo "Cleaning up..."
rm *.log
rm dist/*.exe
rm dist/*.jar
rm dist/*.log
rm bin/*.jar
rm bin/*.log
rm -f -r bin/org
rm -f -r dist/lib
echo "Done."

