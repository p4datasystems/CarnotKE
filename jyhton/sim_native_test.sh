#!/bin/bash
echo "run this script with 'debug' as an argument to debug on port 5100"
if [ -z "$1" ]
  then   
    # dist/bin/jython NoSQLRDFUnitTests.py
    dist/bin/jython nativeSIMunitTests.py

  else
    # dist/bin/jython -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5100 NoSQLRDFUnitTests.py
    dist/bin/jython -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5100 nativeSIMunitTests.py
fi
