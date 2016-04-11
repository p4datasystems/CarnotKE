#!/bin/bash

rm db/*
export INSTANCE_ROOT="."
java -cp "../../lib/je.jar:." wdb.WDB


