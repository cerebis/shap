#/bin/bash

#
# Warning message - configuration may
#
echo -e "\n             WARNING!"
echo -e " This tool expects to be initializing a new database."
echo -e " Existing data may be in conflict."
echo -e " Really run configuration script? (yes\[no])\n"
read ans
if [ "$ans" != "yes" ]
then
	echo "Not running config"
	exit
else
	echo "Running initial configuration setup"
	bin/run.sh org.mzd.shap.spring.cli.ConfigSetup $*
fi
