#/bin/bash
# Find the script parent path
BIN_DIR=`type -p $0`
BIN_DIR=`dirname $BIN_DIR`
# Include config details
. $BIN_DIR/runtime_config.sh
# Launch
$JVM_LAUNCH org.mzd.shap.spring.cli.EmbeddedServer "$@"
