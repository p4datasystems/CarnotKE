#!/bin/bash
echo "Setting INSTALLATION_ROOT path"
export INSTALLATION_ROOT="${PWD}"
echo "run this script with 'debug' as an argument to debug on port 5100"
if [ -z "$1" ]
  then
    dist/bin/jython TitanDBUnitTests.py

  else
    dist/bin/jython -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5100 TitanDBUnitTests.py
export INSTALLATION_ROOT="."
fi
