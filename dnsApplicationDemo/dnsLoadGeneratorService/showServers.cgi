#!/bin/sh

# List the servers

echo "Content-type: text/html"
echo ""

slaveIdx=0
masterIdx=0
grep -v "#" DnsServerList.txt | grep -v "^$" | sed 's/:/ /g' |
while read serverType serverIP
do

	if [ ${serverType} == "Master" ]
	then
		masterServers[${masterIdx}]="${serverIP}"
		masterIdx=`expr ${masterIdx} + 1`
	else
		slaveServers[${slaveIdx}]="${serverIP}"
		slaveIdx=`expr ${slaveIdx} + 1`
	fi

done
cat << EOF
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="10;url=/cgi-bin/showServers.cgi">
<title>DNS Servers List</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<body class="claro">
<table align="center">
EOF

idx=0
while [ ${idx} -lt ${masterIdx} ]
do
	echo "<tr>"
	echo "<td>Master DNS Server</td>"
	echo "<td>${masterServers[${idx}]}</td>"
	echo "</tr>"
	
	idx=`expr ${idx} + 1`
done

idx=0
while [ ${idx} -lt ${slaveIdx} ]
do
	echo "<tr>"
	echo "<td>Slave DNS Server</td>"
	echo "<td>${slaveServers[${idx}]}</td>"
	echo "</tr>"
	
	idx=`expr ${idx} + 1`
done

cat << EOF
</table> 
</body>
</html>
EOF





