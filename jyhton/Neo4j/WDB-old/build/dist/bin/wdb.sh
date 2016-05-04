pwd#!/bin/sh -xv

# Capture the current working directory so that we can change to it later.
# Then capture the location of this script and the Directory Server instance
# root so that we can use them to create appropriate paths.
WORKING_DIR=`pwd`

cd `dirname "${0}"`
SCRIPT_DIR=`pwd`

cd ..
INSTANCE_ROOT=`pwd`
echo "INSTANCE_ROOT is "$INSTANCE_ROOT
export INSTANCE_ROOT

cd "${WORKING_DIR}"

cd "${INSTANCE_ROOT}"
cd ..
CLASSPATH="`pwd`"/classes
for JAR in "${INSTANCE_ROOT}"/lib/*.jar
do
  CLASSPATH="${CLASSPATH};${JAR}"
done
export CLASSPATH

#cd ..
#cd ..
#cd classes
echo "Im in"`pwd`
echo $CLASSPATH

"${JAVA_HOME}"/bin/java wdb.WDB
