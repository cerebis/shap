#!/bin/bash
#
# Simple wrapper which translates Metagene's output format
# into XML and also better announces runtime errors by returning
# non-zero exit values where Metagene does not.
#
# This XML is supported by the Betwixt based Java package
# mzd.analysis.metagene.bean
#
# MZD 17/07/2008
#

function assertExists {
	type -P $1 &> /dev/null || { echo "$1 does not appear to be installed" >&2; exit 1; }
}

#
# Metagene is distributed as a binary for 2 architectures.
#
# 32 bit version
METAGENE="mga_linux_ia32"
# 64 bit version
#METAGENE="mga_linux_ia64"

# Check for the command
# Comment this out if you choose to use an explicit path.
assertExists $METAGENE

if [ $# -ne 2 ]
then
	echo "Usage: metageneXML.sh <input metagene file> <output metagene XML>"
	exit 1
fi

if [ -d $2 ]
then
	echo "The output path '$2' is a directory"
	exit 1
fi

# Call metagene and pipe stderr and stdout to awk script
$METAGENE $1 2>&1 | grep -v "Input sequences will be individually treated." | awk -v outfile=$2 '

function modelFlagToName(flag) {
	if (flag == "-") {
		return "UNDEFINED"
	} else if (flag == "a") {
		return "ARCHAEA"
	} else if (flag == "b") {
		return "BACTERIA"
	} else if (flag == "p") {
		return "PHAGE"
	}
	else {
		return "UNDEFINED"
	}
}

function nullSafe(field) {
	return field == "-" ? "" : field;
}

function translateHeader() {
	match($0,/^# ([^ ]+)(.*)$/,query)
	
	getline
	match($0,/^# gc = (.*), rbs = (.*)$/,content)
	
	getline
	match($0,/^# self: (.)/,domain)
	
	print "\t\t<id>" query[1] "</id>" >> outfile
	print "\t\t<desc>" query[2] "</desc>" >> outfile
	print "\t\t<gc>" content[1] "</gc>" >> outfile
	print "\t\t<rbs>" content[2] "</rbs>" >> outfile
	print "\t\t<domain>" modelFlagToName(domain[1]) "</domain>" >> outfile
}

# Truncate any pre-existing output file
BEGIN {
	numSeq = 0
	print "<metagene>" > outfile
}

# Reformat the output for more easily parsed multifasta
{
	if ($0 ~ /^Wrong file/) {
		print $0 > "/dev/stderr"
		exit 1
	}
	
	if ($0 ~ /^#/) {
		if (numSeq++ > 0) {
			print "\t</sequence>" >> outfile
		}
		print "\t<sequence>" >> outfile
		translateHeader()
	}
	else if (length($0) > 0) {
		printf("\t\t<orf start=\"%s\" stop=\"%s\" strand=\"%s\" frame=\"%s\" conf=\"%s\" partial=\"%s\" model=\"%s\" rbs-start=\"%s\" rbs-stop=\"%s\" rbs-score=\"%s\"/>\n",
			$2,$3,$4,$5,$7,$6 ~ /0/ ? "true" : "false", modelFlagToName($8), nullSafe($9), nullSafe($10), nullSafe($11)) >> outfile
	}
}

END {
	if (numSeq > 0) {
		print "\t</sequence>" >> outfile
	}
	print "</metagene>" >> outfile
}'
