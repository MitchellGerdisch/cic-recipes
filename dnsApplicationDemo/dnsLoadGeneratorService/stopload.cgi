#!/bin/sh
echo "Content-type: text/html"
echo ""

# check if there are any load geneator shell processes running
dnsLoadGenPID=`ps -eo pid,args |grep dnsLoadGenerator.sh|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null
while [ ! -z "${dnsLoadGenPID}" ]
do
	# If so, then handle the case where for some reason multiple instances of the process are spawned.
	# So look for all of them and kill each one.
    ps -eo pid,args |grep dnsLoadGenerator.sh|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1 |
    while read dnsLoadGenPID
    do
            #echo "killing process ${dnsLoadGenPID}"
            kill -9 ${dnsLoadGenPID} > /dev/null
    done
    dnsLoadGenPID=`ps -eo pid,args |grep dnsLoadGenerator.sh|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null
done
#echo "all gone"

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
