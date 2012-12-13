#!/bin/sh

# start up haproxy
service haproxy start

# set up haproxy to start on reboot
chkconfig haproxy on

