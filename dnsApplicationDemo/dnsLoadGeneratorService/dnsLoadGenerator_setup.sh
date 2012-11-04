#!/bin/sh

# This script sets things up for the dnsLoadGenerator to use the nova client which accesses the CloudBand northbound OpenStack API
# It is executed during the prestart.
#


# these environment variables are used by nova client when called
export OS_USERNAME="${1}"
export OS_PASSWORD="${2}"
export OS_TENANT_NAME="${3}"
export OS_AUTH_URL="${4}"

# install python and the nova client 
# install python 2.6
yum install -y python;
# configure repository to get the client
rpm -Uvh http://mirrors.servercentral.net/fedora/epel/6/x86_64/epel-release-6-7.noarch.rpm;
# install novaclient
yum install -y python-novaclient;