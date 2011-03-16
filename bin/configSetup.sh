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
	bin/run.sh org.mzd.shap.spring.cli.ConfigSetup $*
fi
