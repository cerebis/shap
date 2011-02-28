#/bin/bash
#
# Warning message - configuration may
#
echo -e "\n             WARNING!"
echo -e " Existing index will be recreated, this may take some time to complete."
echo -e " Really run mass indexing? (yes\[no])\n"
read ans
if [ "$ans" != "yes" ]
then
	echo "Not indexing"
	exit
else
	echo "Removing old index"
	rm -rf lucene
	echo "Starting mass indexing"
	bin/run.sh org.mzd.shap.spring.cli.Index
fi
