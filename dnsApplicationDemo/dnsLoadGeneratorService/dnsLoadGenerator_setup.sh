#!/bin/sh

# This script sets things up for the dnsLoadGenerator to use the nova client which accesses the CloudBand northbound OpenStack API
# It is executed during the prestart.
#


# these environment variables are used by nova client when called
OS_USERNAME="${1}"
OS_PASSWORD="${2}"
OS_TENANT_NAME="${3}"
OS_AUTH_URL="${4}"

NOVA_WRAPPER="${5}"

# install python and the nova client 
# install python 2.6
yum install -y python;
# configure repository to get the client
rpm -Uvh http://mirrors.servercentral.net/fedora/epel/6/x86_64/epel-release-6-7.noarch.rpm;
# install novaclient
yum install -y python-novaclient;

# build a nova wrapper script that has the credentials prebuilt in there
echo "nova --os_username '${OS_USERNAME}' --os_password '${OS_PASSWORD}' --os_tenant_name '${OS_TENANT_NAME}' --os_auth_url '${OS_AUTH_URL}' --insecure \$*" > ${NOVA_WRAPPER}

# make it executable
chmod +x ${NOVA_WRAPPER}

# install python psutil package
yum install -y python-psutil;

# install dnsperf which used to generate dns load
yum install -y dnsperf;