#!/bin/sh

# Capture the current working directory so that we can change to it later.
# Then capture the location of this script and the Directory Server instance
# root so that we can use them to create appropriate paths.
WORKING_DIR=`pwd`

cd `dirname "${0}"`
SCRIPT_DIR=`pwd`

cd ..
INSTANCE_ROOT=`pwd`
export INSTANCE_ROOT

cd "${WORKING_DIR}"

CLASSPATH=${INSTANCE_ROOT}/classes
for JAR in ${INSTANCE_ROOT}/lib/*.jar
do
  CLASSPATH=${CLASSPATH}:${JAR}
done
export CLASSPATH

${JAVA_HOME}/bin/java wdb.WDB
