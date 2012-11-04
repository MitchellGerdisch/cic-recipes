#!/bin/sh

echo "Content-type: text/html"
echo ""

cat << EOF 
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="10;url=/cgi-bin/showServers.cgi">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<body>

<table align="center">

EOF

grep -v "#" DnsServerList.txt | grep -v "^$" | sed 's/:/ /g' |
while read serverType serverIP
do
	echo "<tr>"
	echo "<td>$serverType</td>"
	echo "<td>$serverIP</td>"
	echo "</tr>"
done

cat << EOF
</table> 
</body>
</html>
EOF


