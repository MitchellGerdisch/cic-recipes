#!/bin/bash

# args:
# $1 the error code of the last command (should be explicitly passed)
# $2 the message to print in case of an error
# 
# an error message is printed and the script exists with the provided error code
function error_exit {
	echo "$2 : error code: $1"
	exit ${1}
}


echo "Using yum. Installing httpd on one of the following : Red Hat, CentOS, Fedora, Amazon"
yum install -y -q httpd || error_exit $? "Failed on: sudo yum install -y -q httpd"
	
service httpd start

echo "You have reached a remote node apache deployment<P>" > /var/www/html/index.html
ifconfig  | grep "inet addr" | grep 135 >> /var/www/html/index.html
 


