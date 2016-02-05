#!/bin/bash
ant clean
rm -rf dist/
ant
ant
dist/bin/jython -J-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 nativeSIMunitTests.py
