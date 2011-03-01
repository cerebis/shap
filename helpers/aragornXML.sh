#!/bin/bash
#
# Simple wrapper which translates Aragorn's batch output format
# into XML.
#
# This XML is supported by the Betwixt based Java package
# mzd.analysis.aragorn.bean
#
# MZD 18/07/2008
#

if [ $# -ne 2 ]
then
        echo "Usage: aragornXML.sh <input fasta file> <output file>"
        exit 1
fi

if [ ! -r $1 ]
then
	echo "The input path '$1' does not exist or is not readable"
	exit 1
fi

if [ -d $2 ]
then
        echo "The output path '$2' is a directory"
        exit 1
fi

/array/informatics/bin/aragorn -t -i -w 2>&1 $1 | awk -v outfile=$2 '

BEGIN {
	print "<aragorn>" > outfile
}
{
	if ($0 ~ /^Could not open/) {
		print $0 > "/dev/stderr"
		exit(1)
	}
	if ($0 ~ /^>end/) {
		exit(0)
	}
	
	# Header
	else if ($0 ~ "^>") {
		id = substr($1,2)
		desc = substr($0,length($1)+2)
		
		print "\t<sequence>" >> outfile
		print "\t\t<id>"id"</id>" >> outfile
		print "\t\t<desc>"desc"</desc>" >> outfile
		
		getline
		numGenes = $1
		if (numGenes > 0) {
			n = 0
			while (n<numGenes && getline) {
				match($3,/(c?)\[([0-9]+),([0-9]+)\]/,locus)
				match($5,/^\(([\.[:alpha:]]+)\)(i\(([0-9]+),([0-9]+)\))?$/,ac)
				if (length(ac[2])>0) {
					intron = " intron-position=\"" ac[3] "\" intron-length=\"" ac[4] "\""
				}
				else {
					intron = ""
				}
				print "\t\t<gene start=\"" locus[2] "\"" \
						" stop=\"" locus[3] "\"" \
						" strand=\"" (locus[1] == "c" ? "-" : "+") "\"" \
						" species=\"" $2 "\"" \
						" anticodon=\"" ac[1] "\"" \
						" anticodon-position=\"" $4 "\"" \
						intron "/>" >> outfile
				n++
			}
		}
		print "\t</sequence>" >> outfile
	}
}
END {
	print "</aragorn>" >> outfile
}'
