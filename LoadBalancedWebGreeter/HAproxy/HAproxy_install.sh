#!/bin/sh

# point at the repo where haproxy is found
wget http://images.cic.cloud-band.com/images/haproxy-1.4.22-1.el6.x86_64.rpm

rpm -Uvh haproxy-1.4.22-1.el6.x86_64.rpm

# install HAproxy
yum --nogpgcheck localinstall haproxy-1.4.22-1.el6.x86_64.rpm 

# disable httpd
service httpd stop
chkconfig httpd off