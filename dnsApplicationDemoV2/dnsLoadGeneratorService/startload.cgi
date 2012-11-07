#!/bin/sh

echo "Content-type: text/html"
echo ""

# how long to run test in seconds
testRunLength=300

# list of names to resolve
# the recipe should have installed this file.
resolver_list="/var/www/cgi-bin/resolverList.dat"

# get the server IP passed in
if [ "${QUERY_STRING}" == "" ]
then
        if [ $# -ne 1 ]
        then
                echo "If not called from web page, you need to pass the server IP"
                exit 1
        fi

        server_ip="$1"
else
        server_ip=`echo ${QUERY_STRING} | cut -d"=" -f2`
fi


# run load generator with the given IP address
/var/www/cgi-bin/dnsLoadGenerator.sh ${server_ip} ${resolver_list} ${testRunLength} > /dev/null &

cat << EOF
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="refresh" content="15;url=/cgi-bin/paintStopPage.cgi">
<title>DNS Application Scaling Demo Load Generator</title>
<link rel="stylesheet" href="/cb_style.css" media="screen">
</head>
<P>
<body>
<H3>
Generating DNS load against server at: ${server_ip} ....
</H3>
</body>

</html>
EOF
