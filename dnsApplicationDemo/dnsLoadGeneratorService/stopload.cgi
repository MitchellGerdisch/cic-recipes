#!/bin/sh
echo "Content-type: text/html"
echo ""
echo "<html>"

echo "Stopping DNS load."

dnsLoadGenPID=`ps -eo pid,args |grep dnsLoadGenerator.sh|sed 's/^ *//g'|sed 's/  */:/g'|grep -v "grep" | cut -d":" -f1` > /dev/null

kill -9 ${dnsLoadGenPID} > /dev/null

cat <<EOF
<form name="startload" action="/cgi-bin/startload.cgi" method="get">
DNS Server IP: <input type="text" name="serverip">
<input type="submit" value="Start Load">
</form>
EOF


echo "</html>"
