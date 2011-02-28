#/bin/bash

#
# Warning message - configuration may
#
echo -e "\n             WARNING!"
echo -e " All existing data in database will be lost!"
echo -e " Really run configuration script? (yes\[no])\n"
read ans
if [ "$ans" != "yes" ]
then
	echo "Not running config"
	exit
else
	echo "Running initial configuration setup"
	bin/run.sh org.mzd.shap.spring.cli.ConfigSetup
fi
