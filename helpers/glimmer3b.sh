#!/bin/bash

#
# Comment out the statement below once paths are set for:
#  - Glimmer scripts folder
#  - Glimmer bin folder
#  - Elph executable
#
echo -e "Please modify the script before using.\nYou need to set paths"; exit 1

AWKPATH=/usr/local/glimmer3.02/scripts
GLIMMERPATH=/usr/local/glimmer3.02/bin
ELPH=/usr/local/ELPH/bin/Linux-i386/elph

if [ $# -ne 2 ]
then
	echo "Usage: ${0##*/} <input_fasta> <output_file>"
	exit 1
fi

GENOME=$1
TAG=`basename $2`
OUTDIR=`dirname $2`

# add/change glimmer options here
GLIMMEROPTS="-o50 -g110 -t30"

# Find long, non-overlapping orfs to use as a training set
echo
echo "======================================"
echo "Step 1: Finding long orfs for training"
echo "======================================"
echo
$GLIMMERPATH/long-orfs -n -t 1.15 $GENOME $OUTDIR/$TAG".longorfs"
if [ $? -ne 0 ]
then
  echo "Failed to find long-orf training set"
  exit 1
fi

# Extract the training sequences from the genome file
echo
echo "====================================="
echo "Step 2: Extracting training sequences"
echo "====================================="
echo
$GLIMMERPATH/extract -t $GENOME $OUTDIR/$TAG".longorfs" > $OUTDIR/$TAG".train"
if [ $? -ne 0 ]
then
  echo "Failed to extract training sequences"
  exit 1
fi


# Build the icm from the training sequences
echo
echo "===================="
echo "Step 3: Building ICM"
echo "===================="
echo
$GLIMMERPATH/build-icm -r $OUTDIR/$TAG".icm" < $OUTDIR/$TAG".train"
if [ $? -ne 0 ]
then
  echo "Failed to build ICM"
  exit 1
fi

# Run first Glimmer
echo
echo "==============================="
echo "Step 4:  Running first Glimmer3"
echo "==============================="
echo
$GLIMMERPATH/glimmer3 $GLIMMEROPTS $GENOME $OUTDIR/$TAG".icm" $OUTDIR/$TAG".run1"
if [ $? -ne 0 ]
then
  echo "Failed to run Glimmer3"
  exit 1
fi

# Get training coordinates from first predictions
echo
echo "====================================="
echo "Step 5:  Getting training coordinates"
echo "====================================="
echo
tail -n +2 $OUTDIR/$TAG.run1.predict > $OUTDIR/$TAG.coords
if [ $? -ne 0 ]
then
  echo "Failed to get training coordinates"
  exit 1
fi

# Create a position weight matrix (PWM) from the regions
# upstream of the start locations in $TAG.coords
echo
echo "========================================="
echo "Step 6:  Making PWM from upstream regions"
echo "========================================="
echo
$AWKPATH/upstream-coords.awk 25 0 $OUTDIR/$TAG".coords" | $GLIMMERPATH/extract $GENOME - > $OUTDIR/$TAG".upstream"
$ELPH $OUTDIR/$TAG".upstream" LEN=6 | $AWKPATH/get-motif-counts.awk > $OUTDIR/$TAG".motif"
if [ $? -ne 0 ]
then
  echo "Failed to create PWM"
  exit 1
fi

# Determine the distribution of start-codon usage in $TAG.coords
echo
echo "=================================="
echo "Step 7:  Getting start-codon usage"
echo "=================================="
echo
startuse=`$GLIMMERPATH/start-codon-distrib -3 $GENOME $OUTDIR/$TAG".coords"`

# Run second Glimmer
echo
echo "================================"
echo "Step 8:  Running second Glimmer3"
echo "================================"
echo
$GLIMMERPATH/glimmer3 $GLIMMEROPTS -b $OUTDIR/$TAG".motif" -P $startuse $GENOME $OUTDIR/$TAG".icm" $OUTDIR/$TAG
if [ $? -ne 0 ]
then
  echo "Failed to run Glimmer3"
  exit 1
fi

# Keep the final prediction
mv "$OUTDIR/$TAG.predict" "$OUTDIR/$TAG"

# Remove all scratch files
find $OUTDIR -maxdepth 1 -name "$TAG.*" -exec rm {} \;

exit 0
