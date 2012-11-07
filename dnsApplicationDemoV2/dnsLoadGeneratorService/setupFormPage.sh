#!/bin/bash

# args:
# $1 The IP of the DNS server - or a string telling the user to enter the IP of the DNS server
# 


dnsServer=$1


# args:
# $1 the error code of the last command (should be explicitly passed)
# $2 the message to print in case of an error
# 
# an error message is printed and the script exists with the provided error code
function error_exit {
	echo "$2 : error code: $1"
	exit ${1}
}

# Build the main form page
cat <<EOF > /var/www/html/index.html
<html>

<form name="startload" action="cgi-bin/startload.cgi" metho="get">
DNS Slave Server IP: <input type="text" name="serverip" value="${dnsServer}">
<input type="submit" value="Start Load">
</form>

</html>

EOF

# Build
export PATH=$PATH:/usr/sbin:/sbin || error_exit $? "Failed on: export PATH=$PATH:/usr/sbin:/sbin"

httpdLocation=`whereis httpd`
for i in ${httpdLocation}
do    
	if [ -d "$i" ] ; then
		currConf="$i/conf/httpd.conf"		
		if [ -f "${currConf}" ] ; then
			echo "Conf is in ${currConf}"
			echo "Replacing $origPort with $newPort..."
			sudo sed -i -e "s/$origPort/$newPort/g" ${currConf} || error_exit $? "Failed on: sudo sed -i -e $origPort/$newPort"
			endOfFile="\/VirtualHost>"
			echo "End of ${currConf} replacements"
			
			echo "Chmodding -R 777 $i"
			sudo chmod -R 777 $i
									
			exit 0
		fi	
    fi	
done
