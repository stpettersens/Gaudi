#!/bin/bash
# Just compile the small supporting
# programs used by the installer.
# 
# 1) JavaCheck - to find a JVM and its version
# 2) FindInPath - to find a file in a system variable and its path

echo "Compiling supporting programs for installer..."
cd dist
javac -deprecation -verbose -classpath . JavaCheck.java FindInPath.java
echo "Done"
