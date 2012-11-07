#!/usr/bin/python
import subprocess
import psutil
import sys

print "Content-type: text/html"
print

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

print '''<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="refresh" content="5;url=/cgi-bin/paintStopPage.cgi">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>'''

if num_slaves < 2:

	print '''<body class="claro">
    <table align="center" width="100%">
    <tr>
    <td>
    <form name="stopload" action="/cgi-bin/stopload.cgi" method="get">
    DNS Slave service scale-out in progress ...
    <br>
    <input type="submit" value="Abort Demo">
    </form>
    </td>
    </tr>'''

else:

	print '''<body class="claro">
	<table align="center" width="100%">
	<tr>
	<td>
	<form name="stopload" action="/cgi-bin/stopload.cgi" method="get">
	DNS Slave Service scale out complete.
	<br>
	<input type="submit" value="Stop Load">
	</form>
	</td>
	</tr>'''
        
        
print '''<tr>
<td>
<iframe src="/cgi-bin/showServers.cgi" width="100%" height="150">
</iframe>
</td>
</tr>
</body>
</html>'''
    
