#!/bin/sh

#load constants from script parameters
IPK_FILE=$1
IPK_NAME=`basename "$IPK_FILE"`
IPK_ID=$2

#copy arch.conf if needed
if [ ! -f /media/cryptofs/apps/etc/ipkg/arch.conf ] ; then
	/bin/mkdir -m 777 -p /media/cryptofs/apps/etc/ipkg
	/bin/cp -f /etc/ipkg/arch.conf /media/cryptofs/apps/etc/ipkg/arch.conf
fi

#copy ipk file to temporary location as service request will delete file
/bin/mkdir -m 777 -p /media/internal/.developer
/bin/cp -f "$IPK_FILE" "/media/internal/.developer/$IPK_NAME"

#make sure ApplicationInstallerUtility is not busy
pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
if [ ${#pid} -ne 0 ] ; then
	echo "ERROR: System is busy. Try again later"
	/bin/rm -f "/media/internal/.developer/$IPK_NAME"
	exit 1
fi

#call luna-send to begin installation
/usr/bin/luna-send -n 1 palm://com.palm.appinstaller/installNoVerify {\"target\":\"/media/internal/.developer/$IPK_NAME\"} 2> /tmp/internalz-ipkg.log

#wait for install process to start
pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
while [ ${#pid} -eq 0 ] ; do
	usleep 250
	pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
done

#wait for install process to finish
pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
while [ ${#pid} -ne 0 ] ; do
	usleep 250
	pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
done

#check if install succeeded, then run postinst if needed
if [ -f /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.control ] ; then
	/bin/rm -f /tmp/internalz-ipkg.log
	if [ -f /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.prerm ] ; then
		if [ ! -f /media/cryptofs/apps/.scripts/$IPK_ID/pmPreRemove.script ] ; then
			/bin/mkdir -m 777 -p /media/cryptofs/apps/.scripts/$IPK_ID
			/bin/cp -f /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.prerm /media/cryptofs/apps/.scripts/$IPK_ID/pmPreRemove.script
		fi
	fi
	if [ -f /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.postinst ] ; then
		/bin/sh -c "export IPKG_OFFLINE_ROOT=/media/cryptofs/apps ; /bin/sh /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.postinst"
		if [ $? -ne 0 ] ; then
			echo "ERROR: Postinst script failed!"
			sleep 2
			/usr/bin/luna-send -n 1 palm://com.palm.appinstaller/remove {\"packageName\":\"$IPK_ID\"}
			exit 1
		fi
	fi
else
	/bin/cat /tmp/internalz-ipkg.log
	/bin/rm -f /tmp/internalz-ipkg.log
	echo "ERROR: Ipkg install failed!"
	/bin/rm -f "/media/internal/.developer/$IPK_NAME"
	exit 1
fi
