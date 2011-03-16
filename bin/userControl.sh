#/bin/bash
BIN_DIR=`type -p $0`
BIN_DIR=`dirname $BIN_DIR`
$BIN_DIR/run.sh org.mzd.shap.spring.cli.UserControl $*
