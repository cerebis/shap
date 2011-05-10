#/bin/bash
echo -e "WARNING! Existing index will be recreated, this may take some time to complete."
echo -e "Really run mass indexing? yes\[NO]"
read ans
if [ "$ans" != "yes" ]
then
	echo "Not indexing"
	exit
else
	echo "Removing old index"
	rm -rf lucene
	echo "Starting mass indexing"
	# Find the script parent path
	BIN_DIR=`type -p $0`
	BIN_DIR=`dirname $BIN_DIR`
	# Include config details
	. $BIN_DIR/runtime_config.sh
	# Launch
	$JVM_LAUNCH org.mzd.shap.spring.cli.Index
fi

