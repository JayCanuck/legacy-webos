#!/bin/sh

#load constants from script parameters
IPK_ID=$1

#copy arch.conf if needed
if [ ! -f /media/cryptofs/apps/etc/ipkg/arch.conf ] ; then
	/bin/mkdir -m 777 -p /media/cryptofs/apps/etc/ipkg
	/bin/cp -f /etc/ipkg/arch.conf /media/cryptofs/apps/etc/ipkg/arch.conf
fi

#make sure ApplicationInstallerUtility is not busy
pid=`pgrep -f "ApplicationInstallerUtility" | head -n 1`
if [ ${#pid} -ne 0 ] ; then
	echo "ERROR: System is busy. Try again later"
	exit 1
fi

#execute prerm if it exists
if [ -f /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.prerm ] ; then
	/bin/sh -c "export IPKG_OFFLINE_ROOT=/media/cryptofs/apps ; /bin/sh /media/cryptofs/apps/usr/lib/ipkg/info/$IPK_ID.prerm"
	if [ $? -ne 0 ] ; then
		echo "ERROR: Prerm script failed!"
		exit 1
	fi
fi

#uninstall ipk
if [ -f /media/cryptofs/apps/usr/palm/applications/$IPK_ID/appinfo.json ] ; then
	/usr/bin/luna-send -n 3 palm://com.palm.appinstaller/remove {\"packageName\":\"$IPK_ID\",\"subscribe\":true}  2> /tmp/internalz-ipkg.log
	grep -q "FAILED" /tmp/internalz-ipkg.log
else
	/bin/sh /media/cryptofs/apps/.scripts/$IPK_ID/pmPreRemove.script > /dev/null 2>&1
	/usr/bin/ipkg -o /media/cryptofs/apps -force-depends remove $IPK_ID > /tmp/internalz-ipkg.log
	if [ $? -eq 0 ] ; then
		/bin/false
	else
		/bin/true
	fi
fi
	
if [ $? -eq 0 ] ; then
	cat /tmp/internalz-ipkg.log
	rm -f /tmp/internalz-ipkg.log
	echo "ERROR: Ipkg remove failed!"
	exit 1
else
	rm -f /tmp/internalz-ipkg.log
	if [ -d /media/cryptofs/apps/usr/palm/applications/$IPK_ID ] ; then
		/bin/rm -rf /media/cryptofs/apps/usr/palm/applications/$IPK_ID
	fi
fi
