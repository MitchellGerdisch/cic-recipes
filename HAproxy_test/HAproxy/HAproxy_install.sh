#!/bin/sh

# point at the repo where haproxy is found
rpm -Uvh http://download.fedoraproject.org/pub/epel/6/i386/epel-release-6-7.noarch.rpm

# install HAproxy
yum install -y haproxy