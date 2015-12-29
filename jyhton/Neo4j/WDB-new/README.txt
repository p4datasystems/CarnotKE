To build WDB run:
build.sh or build.bat

To run WDB on Unix, do the following:
cd build/classes
$ export INSTANCE_ROOT="."
$ java -cp "../../lib/je.jar:." wdb.WDB

To run Windows on Unix, do the following:
cd build/classes
$ export INSTANCE_ROOT="."
$ java -cp "..\..\lib\je.jar;." wdb.WDB
