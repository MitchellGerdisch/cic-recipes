#!/bin/sh

# A simple test script to show parallel execution

# parameters:
# $1 - number of iterations



if [ $# -ne 2 ]
then
	echo "usage: $0 <NUMBER OF ITERATIONS> <AMOUNT OF TIME TO SLEEP BETWEEN ITERATION>"
	exit
fi

# number of iterations
num_iterations=${1}
# number of seconds to sleep each iteration
sleeptime=${2}

i=0

while [ $i -lt $num_iterations ]
do
	((i=i+1))
	echo "iteration number: ${i}"
	sleep ${sleeptime}
done
