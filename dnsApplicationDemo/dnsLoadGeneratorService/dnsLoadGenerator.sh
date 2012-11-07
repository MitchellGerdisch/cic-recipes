#!/bin/sh


# Uses dnsperf tool to generate load.

if [ $# -ne 3 ]
then
	echo "usage: dnsLoad.sh IP-OF-SLAVE-DNS-SERVER RESOLVER-LIST LENGTH-OF-TEST"
	echo "Where RESOLVER-LIST is a file containing a list of names to resolve"
	echo "Where LENGTH-OF-TEST is time in seconds to run the test"
	exit
fi

dnsperf -s ${1} -d ${2} -l ${3} > /dev/null 2>&1 

