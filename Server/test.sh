
#!/bin/bash
set -e

echo "Cleaning up .class files"
rm -f *.class

echo "Compiling"
javac *.java

echo "Running Tests"
java Test 