#!/bin/sh

if [ $# -ne 2 ]
then
	echo "usage: dnsLoad.sh IP-OF-SLAVE-DNS-SERVER LOAD-AMOUNT"
	echo "Where LOAD-AMOUNT is an integer greater than 0"
	exit
fi

numcycles=0
while [ $numcycles -lt $2 ]
do
	echo "Resolving addresses - cycle $numcycles"
	/opt/java/bin/java -jar ./Resolve.jar $1 > /dev/null
	((numcycles=numcycles+1))
done
