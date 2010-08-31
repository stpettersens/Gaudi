#!/bin/sh
# Clean-up Gaudi generated files

echo "Cleaning up..."
rm *.log
rm bin/*.exe
rm bin/*.jar
rm bin/*.log
rm -f -r bin/org
echo "Done."

