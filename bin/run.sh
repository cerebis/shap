#!/bin/bash
#
# Command-line starter script.
#

if [ -z "$*" ]
then
	echo "No command class supplied"
	exit 1
fi

# SHAP's dependent libraries
PROG_LIBS=`find war/WEB-INF/lib -name '*.jar' | tr '\n' ':'`

# SHAP's class files.
CLASSPATH=$PROG_LIBS"war/WEB-INF/classes"

java -cp $CLASSPATH $*
