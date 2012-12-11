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

export PATH=$PATH:/usr/sbin:/sbin || error_exit $? "Failed on: export PATH=$PATH:/usr/sbin:/sbin"

# Removing ulteo repository file if exist and set new one
echo "Set the repository file on one of the following : Ubuntu, Debian, Mint" 
sudo rm -f /etc/apt/sources.list.d/ulteo-ovd.list || error_exit $? "Failed on: remove repository file"
sudo echo "deb http://archive.ulteo.com/ovd/3.0/ubuntu lucid main" >> /etc/apt/sources.list.d/ulteo-ovd.list || error_exit $? "Failed on: add to repository"

# Removing previous Ulteo* installation if exist
echo "Using sudo apt-get -y -q autoremove ulteo-* on one of the following : Ubuntu, Debian, Mint" 
sudo apt-get -y -q autoremove ulteo-ovd-subsystem  || error_exit $? "Failed on: sudo apt-get -y -q autoremove ulteo-*"

# The existence of the usingAptGet file in the ext folder will later serve as a flag that "we" are on Ubuntu or Debian or Mint
echo "Using apt-get. Updating apt-get on one of the following : Ubuntu, Debian, Mint" 
sudo apt-get -y -q update   || error_exit $? "Failed on: sudo apt-get -y update"
sudo apt-get install -yulteo-keyring  || error_exit $? "Failed on: apt-get install ulteo-keyring"
sudo apt-get -y -q update  || error_exit $? "Failed on: sudo apt-get -y update second time"

#Mysql client installation 
echo "Using sudo apt-get install mysql-server on one of the following : Ubuntu, Debian, Mint"
sudo apt-get install -y mysql-client || error_exit $? "Failed on: sudo apt-get install -y mysql-client"

#Install session manager
echo "Using sudo apt-get install -y ulteo-ovd-session-manager on one of the following : Ubuntu, Debian, Mint"
sudo apt-get install -y ulteo-ovd-subsystem || error_exit $? "Failed on: sudo apt-get install -y ulteo-ovd-session-manager"
