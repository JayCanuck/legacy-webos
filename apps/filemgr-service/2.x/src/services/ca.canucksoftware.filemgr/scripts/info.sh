#!/bin/sh

#load constants from script parameters
IPK_FILE=$1

#get package info
/bin/mkdir -p /var/.temp
cd /var/.temp
/usr/bin/ar -x "$IPK_FILE" control.tar.gz
if [ $? -ne 0 ] ; then
	/bin/rm -fr /var/.temp
	echo "Invalid archive"
	exit 1
fi
/bin/tar xfz control.tar.gz
if [ $? -ne 0 ] ; then
	/bin/rm -fr /var/.temp
	echo "Unable to extract information."
	exit 1
fi
/bin/cat /var/.temp/control
if [ -f /var/.temp/postinst ] ; then
	echo "Has Postinst: Yes"
else
	echo "Has Postinst: No"
fi
if [ -f /var/.temp/prerm ] ; then
	echo "Has Prerm: Yes"
else
	echo "Has Prerm: No"
fi
/bin/rm -fr /var/.temp
