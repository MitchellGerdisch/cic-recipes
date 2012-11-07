#!/usr/bin/python
import subprocess
print "Content-type: text/html"
print

# every time we come into the main page, let's make sure the load has been stopped - just in case.
subprocess.call(['/var/www/cgi-bin/killLoad.cgi'])

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
<meta http-equiv="refresh" content="15;url=/cgi-bin/paintMainPage.cgi">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>'''

if num_slaves < 2:

	print '''<body class="claro">
	<table align="center" width="100%">
	<tr>
	<td>
	<form name="startload" action="/cgi-bin/startload.cgi" method="get">
	Enter Slave DNS Server IP Address and click button to generate load and cause DNS application to scale out.
	<P>'''

 	print "<input type=\"text\" value=\"" + slaveServers[0] + "\" name=\"serverip\">"

	print '''<input type="submit" value="Start Load">
	</form>
	</td>
	</tr>'''

	
else:

	print '''<body class="claro">
	<table align="center" width="100%">
	<tr>
	<td>
	<H3>DNS Slave service scale-in in progress ...</H3>
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
