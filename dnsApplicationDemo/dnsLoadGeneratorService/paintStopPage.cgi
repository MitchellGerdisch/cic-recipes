#!/bin/sh

# Paints main page with an iframe for the server list

echo "Content-type: text/html"
echo ""

# Parse the server list file
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
<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="utf-8">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<P>
<P>
<P>
<body class="claro">
<table>
<tr>
<td>
<form name="startload" action="/cgi-bin/startload.cgi" method="get">
Enter Slave DNS Server IP Address and click button to generate load and cause DNS application to scale in a new server.
<P>
<input type="text" name="serverip">
<input type="submit" value="Start Load">
</form>
</td>
</tr>
<tr>
<td>
<iframe src="/cgi-bin/showServers.cgi" width="100%" height="150">
</iframe>
</td>
</tr>
</body>
</html>
EOF

