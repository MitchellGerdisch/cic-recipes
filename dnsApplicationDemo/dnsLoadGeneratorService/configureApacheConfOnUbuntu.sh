#!/bin/bash

# args:
# $1 The original port in the apache2.conf (ususally 80)
# $2 The required port in the apache2.conf
# $3 Full path of the recipe folder (context.serviceDirectory)
# 


origPort=$1
newPort=$2
serviceDirectory=$3


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

apache2Location=`whereis apache2`
for i in ${apache2Location}
do    
	if [ -d "$i" ] ; then
		portsConf="$i/ports.conf"		
		if [ -f "${portsConf}" ] ; then
			echo "portsConf is in ${portsConf}"					
			echo "Replacing $origPort with $newPort..."
			sudo sed -i -e "s/$origPort/$newPort/g" ${portsConf} || error_exit $? "Failed on: sudo sed -i -e $origPort/$newPort in ${portsConf}"			
			echo "End of ${portsConf} replacements"
			
			defaultFile="$i/sites-available/default"			
			sudo sed -i -e "s/$origPort/$newPort/g" ${defaultFile} || error_exit $? "Failed on: sudo sed -i -e $origPort/$newPort in ${defaultFile}"	
			
			echo "Chmodding -R 777 $i"
			sudo chmod -R 777 $i
										
			exit 0
		fi	
    fi	
done
