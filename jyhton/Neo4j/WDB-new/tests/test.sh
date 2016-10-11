#!/bin/bash

../../build.sh
pwd
export INSTANCE_ROOT="."
java -cp "../../lib/je.jar;." wdb.WDB
