#!/bin/bash
set -e

echo "Cleaning up .class files"
rm -f *.class

echo "Compiling"
javac *.java

echo "Running"
java Server 8080 800 600 100 50 RED GREEN BLUE YELLOW
