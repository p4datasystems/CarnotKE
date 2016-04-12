#!/bin/bash

../../build.sh
export INSTANCE_ROOT="."
java -cp "../../lib/je.jar:." wdb.WDB

