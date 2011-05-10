#/bin/bash

#
# Warning message - configuration may
#
echo -e "WARNING! This tool expects to be initializing a new database."
echo -e "Existing data may be in conflict."
echo -e "Really run configuration script? yes\[NO]"
read ans
if [ "$ans" != "yes" ]
then
	echo "Not running config"
	exit
else
	echo "Running initial configuration setup"
	# Find the script parent path
	BIN_DIR=`type -p $0`
	BIN_DIR=`dirname $BIN_DIR`
	# Include config details
	. $BIN_DIR/runtime_config.sh
	# Launch
	$JVM_LAUNCH org.mzd.shap.spring.cli.ConfigSetup "$@"
fi
