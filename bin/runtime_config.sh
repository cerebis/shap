#!/bin/bash
#
# Command-line runtime configuration details
#

# Set to installed location
SHAP_DIR=.

# SHAP's dependent libraries
PROG_LIBS=`find $SHAP_DIR/war/WEB-INF/lib -name '*.jar' | tr '\n' ':'`

# SHAP's class files.
CLASSPATH=$PROG_LIBS"$SHAP_DIR/war/WEB-INF/classes:$SHAP_DIR/war/WEB-INF/spring"

# Additional options to the JVM
JVM_OPTIONS=-Xmx1024M

# JVM launch command.
# Usage: $JVM_LAUNCH [main class] [args]
JVM_LAUNCH="java $JVM_OPTIONS -cp $CLASSPATH"
