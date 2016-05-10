#!/bin/bash
echo "run this script with 'debug' as an argument to debug on port 5100"
echo "Deleting DB"
rm -rf db/
echo "Deletion Complete"
if [ -z "$1" ]
  then
    dist/bin/jython TitanDBUnitTests.py

  else
    dist/bin/jython -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5100 TitanDBUnitTests.py
fi
