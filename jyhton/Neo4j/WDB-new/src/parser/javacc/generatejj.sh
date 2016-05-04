#!/bin/bash

java -cp ../../../../ext/javacc-4.0/bin/lib/javacc.jar jjtree QueryParser.jjt
java -cp ../../../../ext/javacc-4.0/bin/lib/javacc.jar javacc QueryParser.jj
