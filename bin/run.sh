#!/bin/bash
#
# Command-line starter script.
#

if [ -z "$*" ]
then
	echo "No command class supplied"
	exit 1
fi

# Set to installed location
SHAP_DIR=$HOME/shap

# SHAP's dependent libraries
PROG_LIBS=`find $SHAP_DIR/war/WEB-INF/lib -name '*.jar' | tr '\n' ':'`

# SHAP's class files.
CLASSPATH=$PROG_LIBS"$SHAP_DIR/war/WEB-INF/classes:$SHAP_DIR/war/WEB-INF/spring"

java -cp $CLASSPATH $*
