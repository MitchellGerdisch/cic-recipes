#!/usr/bin/python
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

print '''<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="refresh" content="5;url=/cgi-bin/showServers.cgi">
<title>DNS Servers List</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
<style>
td
{
color:white;
}
</style>
</head>
<P>
<body class="claro">
<table align="center">'''

for server in masterServers:
	print "<tr>"
	print "<td>Master DNS Server</td>"
	print "<td>" + server + "</td>"
	print "</tr>"
	
for server in slaveServers:
	print "<tr>"
	print "<td>Slave DNS Server</td>"
	print "<td>" + server + "</td>"
	print "</tr>"

print '''</table> 
</body>
</html>'''







