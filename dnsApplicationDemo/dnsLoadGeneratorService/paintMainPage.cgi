#!/usr/bin/python
print "Content-type: text/html"
print

# every time we come into the main page, let's make sure the load has been stopped - just in case.
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

masterServers=[]
slaveServers=[]

listFile=open('DnsServerList.txt', 'r')
for line in listFile:
        if line.startswith("Master"):
                masterServers.append(line.split(':')[1])
        if line.startswith("Slave"):
                slaveServers.append(line.split(':')[1])
listFile.close()

num_slaves=len(slaveServers)

if num_slaves < 2:

	print '''<!DOCTYPE HTML>
	<html lang="en">
	<head>
	<meta charset="utf-8">
	<meta http-equiv="refresh" content="15;url=/cgi-bin/paintMainPage.cgi">
	<title>DNS Application Scaling Demo Load Generator</title>
	<link rel="stylesheet" href="/cb_style.css" media="screen">
	</head>
	<P>
	<P>
	<P>
	<P>
	<body class="claro">
	<table align="center" width="100%">
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
	</html>'''
	
else:

	print '''<html>
	<head>
	<meta charset="utf-8">
	<meta http-equiv="refresh" content="15;url=/cgi-bin/paintMainPage.cgi">
	<title>DNS Application Scaling Demo Load Generator</title>
	<link rel="stylesheet" href="/cb_style.css" media="screen">
	</head>
	<body class="claro">
	<table align="center" width="100%">
	<tr>
	<td>
	<H3>Waiting for DNS Slave service to scale in before running next demo.</H3>
	</td>
	</tr>
	<tr>
	<td>
	<iframe src="/cgi-bin/showServers.cgi" width="100%" height="150">
	</iframe>
	</td>
	</tr>
	</body>
	</html>'''




