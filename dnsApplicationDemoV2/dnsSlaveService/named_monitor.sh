#!/bin/sh

# This script acts as a monitor for a DNS bind server.
# It calculates the number of requests since the last time it was called and returns the delta

# This file name must match what is configured in /etc/named.conf
named_stats_file="/var/named/named.stats"

# named stats command
rndc_cmd="rndc stats"

# if named_stats_file does not exist create one
if ! [ -a ${named_stats_file} ]
then
        # run command to generate one.
        ${rndc_cmd}
fi

# get stats from file to get previous count
previous_request_count=`grep "IPv4 requests" ${named_stats_file} | sed 's/^ *//g' |cut -d" " -f1`
if [ -z ${previous_request_count} ]
then
        # first time so just set to 0
        previous_request_count=0
fi

# now remove the file and rerun stats and get new count
rm -f ${named_stats_file}
${rndc_cmd}
# get stats from file to compare to the previous data
new_request_count=`grep "IPv4 requests" ${named_stats_file} | sed 's/^ *//g' |cut -d" " -f1`
if [ -z ${new_request_count} ]
then
        # first time so just set to 0
        new_request_count=0
fi

request_delta=`expr ${new_request_count} - ${previous_request_count}`

# Now calculate the difference between now and last time and return that delta as a mapped pair
printf "${request_delta}"
