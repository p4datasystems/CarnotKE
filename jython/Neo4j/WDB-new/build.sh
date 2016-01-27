#!/bin/sh

# Change to the location of this build script.
cd `dirname $0`

ANT_HOME=`pwd`/ext/apache-ant-1.7.0
export ANT_HOME
# Execute the ant script and pass it any additional command-line arguments.
ext/apache-ant-1.7.0/bin/ant --noconfig ${*}

