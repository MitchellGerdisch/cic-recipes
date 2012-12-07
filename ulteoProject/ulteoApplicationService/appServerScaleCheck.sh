#!/bin/sh
# 

# queries for number of active sessions from utleo service manager DB
#
# parameters:
#   $1 - IP address of the ulteo session manager VM (or wherever the MySQL DB is hosted)
#   $2 - mysql username to use
#   $3 - mysql password to use

mysql_vm_ip=${1}
mysql_username=${2}
mysql_password=${3}

num_sessions=`mysql -u"${mysql_username}" -p"${mysql_password}" -h"${mysql_vm_ip}" --skip-column_names -e 'select count(*) from ovd.ulteo_sessions;' | grep "\|"`

echo "${num_sessions}"