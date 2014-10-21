#!/bin/bash
LIB=./dependency
CLASSPATH=$(find "$LIB" -name '*.jar' -printf '%p:' | sed 's/:$//')
CLASSPATH=$(find . -maxdepth 1 -name '*.jar' -printf '%p:'):${CLASSPATH}
echo ${CLASSPATH}
/usr/bin/java -cp "${CLASSPATH}" eu.ibutler.affiliatenetwork.MainClass

