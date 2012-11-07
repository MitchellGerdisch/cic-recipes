#!/usr/bin/python
import subprocess
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

if num_slaves < 2:

        print '''<!DOCTYPE HTML>
        <html lang="en">
        <head>
        <meta charset="utf-8">
        <meta http-equiv="refresh" content="5;url=/cgi-bin/paintStopPage.cgi">
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
        <form name="stopload" action="/cgi-bin/stopload.cgi" method="get">
        Waiting for DNS Slave service to scale out ...
        <br>
        <input type="submit" value="Abort Demo">
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

        print '''<!DOCTYPE HTML>
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
        <table align="center" width="100%">
        <tr>
        <td>
        <form name="stopload" action="/cgi-bin/stopload.cgi" method="get">
        DNS Service has scaled out.
        <br>
        <input type="submit" value="Stop Load">
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
