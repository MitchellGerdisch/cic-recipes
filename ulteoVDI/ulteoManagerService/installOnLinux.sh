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

echo "Removing old installation in case it's exits " 
yum remove -y ulteo-ovd-session-manager || error_exit $? "Failed on: yum remove -y ulteo-ovd-session-manager"
rm -rf /etc/ulteo/sessionmanager
yum remove -y mysql 	|| error_exit $? "Failed on: yum remove -y mysql"
rm -rf /var/lib/mysql /var/lib/mysql/mysql /usr/bin/mysql || error_exit $? "Failed on: rm -rf /var/lib/mysql /var/lib/mysql/mysql /usr/bin/mysql"

echo "Using yum. Updating yum on one of the following : Red Hat, CentOS, Fedora, Amazon. " 
#yum -y -q update || error_exit $? "Failed on: sudo yum -y -q update"

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

echo "Using yum. Installing mysql on one of the following : Red Hat, CentOS, Fedora, Amazon"
yum install -y mysql mysql-server 	|| error_exit $? "Failed on:yum install -y mysql mysql-server"

echo "Starting Mysql service"
service mysqld restart || error_exit $? "Failed on:service mysqld start"

echo "Set Mysql root Password"
mysqladmin -u root password 'root'  || error_exit $? "Failed on:mysqladmin -u root password root"
	
echo "Create Mysql database"
mysql -u root  --password=root -e 'create database ovd'  || error_exit $? "Failed on:mysql -u root --password=root -e 'create database ovd'"

echo "Yum install ulteo-ovd-session-manager"
yum install -y ulteo-ovd-session-manager  || error_exit $? "Failed on: yum install -y ulteo-ovd-session-manager"

echo "Config ulteo-ovd-session-manager"
#sm-config --chroot-uri http://www.ulteo.com/main/downloads/ulteo-ovd.php?suite=3.0 --chroot-dir /var/cache/ulteo/sessionmanager/base.tar.gz --admin-login admin --admin-pwd admin --assume-yes < args.dat  || error_exit $? "Failed on: sudo sm-config arguments.dat"
sm-config --chroot-uri http://dcb-1-images.cic.cloud-band.com/images/paas/ulteo/base.tar.gz  --chroot-dir /var/cache/ulteo/sessionmanager/base.tar.gz --admin-login admin --admin-pwd admin --assume-yes < args.dat  || error_exit $? "Failed on: sudo sm-config arguments.dat"

echo "Executing the following query to Grant Access"
qry="GRANT ALL ON *.* TO root@'%' IDENTIFIED BY 'root'"
mysql -uroot -proot << eof
$qry
eof

ps -ef | grep -iE "mysqld" | grep -vi grep

echo "Install Web Client"
yum install -y ulteo-ovd-web-client ulteo-ovd-web-client-ajaxplorer || error_exit $? "Failed on:yum install -y ulteo-ovd-web-client ulteo-ovd-web-client-ajaxplorer"

echo "Configure Web Client"
ovd-webclient-config  --sm-address 127.0.0.1  || error_exit $? "Failed on:ovd-webclient-config  --sm-address 127.0.0.1"

