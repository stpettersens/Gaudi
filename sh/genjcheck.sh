#!/bin/bash
# Just compile the small Java version check assisting
# program used by the installer with javac.
# http://java.sun.com

echo "Compiling Java check program..."
cd dist
javac -deprecation -verbose -classpath . JavaCheck.java
echo "Done"

