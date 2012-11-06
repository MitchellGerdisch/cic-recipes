#!/bin/sh
echo "Content-type: text/html"
echo ""

dnsLoadGenPID=`ps -eo pid,args |grep dnsLoadGenerator.sh|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null

kill -9 ${dnsLoadGenPID} > /dev/null

cat << EOF
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="refresh" content="15;url=/cgi-bin/paintMainPage.cgi">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<body>
<H3>
Stopping load against DNS server ....
</H3>
</body>
</html>
EOF
