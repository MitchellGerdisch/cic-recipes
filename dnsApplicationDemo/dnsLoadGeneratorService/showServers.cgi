#!/bin/sh

echo "Content-type: text/html"
echo ""

cat << EOF 
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="15;url=/index.html">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<body>
<H3>
DNS Servers
</H3>
<table>
<tr>
<td>Server Type</td>
<td>Server IP</td>
</tr>

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


