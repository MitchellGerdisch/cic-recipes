#!/bin/sh
echo "Content-type: text/html"
echo ""

./killLoad.sh

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
