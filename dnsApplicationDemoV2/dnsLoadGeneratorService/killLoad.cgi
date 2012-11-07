#!/bin/sh

# A script to go through and kill load generator processes

proc2kill="dnsperf"

# check if there are any load geneator shell processes running
dnsLoadGenPID=`ps -eo pid,args |grep ${proc2kill}|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null
while [ ! -z "${dnsLoadGenPID}" ]
do
        # If so, then handle the case where for some reason multiple instances of the process are spawned.
        # So look for all of them and kill each one.
    ps -eo pid,args |grep ${proc2kill}|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1 |
    while read dnsLoadGenPID
    do
		# echo "killing ${proc2kill} process ${dnsLoadGenPID}"
        kill -9 ${dnsLoadGenPID} > /dev/null

    done
    dnsLoadGenPID=`ps -eo pid,args |grep ${proc2kill}|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null
done
# echo "all gone"
