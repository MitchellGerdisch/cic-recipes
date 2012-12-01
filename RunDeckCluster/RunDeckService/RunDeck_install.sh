#!/bin/sh

# install rundeck 
echo "Setting up rpm to repo.rundeck.org/latest.rpm"
rpm -Uvh http://repo.rundeck.org/latest.rpm

echo "yum installing rundeck"
yum install -y rundeck