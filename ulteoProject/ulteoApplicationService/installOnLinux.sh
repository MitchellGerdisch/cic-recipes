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

#echo "Disable SELinux " 
echo 0 > /selinux/enforce   || error_exit $? "Failed on: echo 0 > /selinux/enforce"

echo "Removing old installation and Samba" 
yum remove -y ulteo-ovd-subsystem || error_exit $? "Failed on: yum remove -y ulteo-ovd-subsystem"
yum remove -y samba  || error_exit $? "Failed on: yum remove -y samba"

echo "Setup the repository file"
rm -f /etc/yum.repos.d/ovd.repo
echo "[ovd-3.0]" >> /etc/yum.repos.d/ovd.repo
echo "name=Ulteo OVD 3.0" >> /etc/yum.repos.d/ovd.repo

# Need to use the right repository from Ulteo depending on Redhat/Centos release
redhat_release=`grep release /etc/redhat-release | sed 's/[A-Z]*[a-z]*[()]*//g' | sed 's/^ *//g' | cut -d"." -f1`
if [ "${redhat_release}" == "6" ]
then
        ovd_repo_release="6.0"
else
        ovd_repo_release="5.5"
fi

echo "baseurl=http://archive.ulteo.com/ovd/3.0/rhel/${ovd_repo_release}/" >> /etc/yum.repos.d/ovd.repo
echo "enabled=1" >> /etc/yum.repos.d/ovd.repo
echo "gpgcheck=1" >> /etc/yum.repos.d/ovd.repo
echo "gpgkey=http://archive.ulteo.com/ovd/keyring" >> /etc/yum.repos.d/ovd.repo

echo "Installing mysql client"
yum install -y mysql || error_exit $? "Failed on: yum install -y mysql"

echo "Yum install ulteo-ovd-subsystem"
yum install -y ulteo-ovd-subsystem || error_exit $? "Failed on: yum install -y ulteo-ovd-subsystem"

echo "Config ovd-subsystem-config with SM = " $1
ovd-subsystem-config --chroot-uri http://dcb-1-images.cic.cloud-band.com/images/paas/ulteo/base.tar.gz --chroot-dir /opt/ulteo --sm-address $1  < args.dat || error_exit $? "Failed on: ovd-subsystem-config --chroot-uri http://10.45.4.64/gigaspaces-repo-test/base.tar.gz --chroot-dir /opt/ulteo --sm-address $1  < args.dat"

echo "Stoping ulteo-ovd-subsystem before Start lifecycle"
service ulteo-ovd-subsystem stop  || error_exit $? "Failed on: service ovd-subsystem-config stop"


