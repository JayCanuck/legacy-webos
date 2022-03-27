#!/bin/sh

if [ -f /usr/bin/fileindexer ] ; then
	/sbin/stop fileindexer
	/bin/rm -f /var/luna/data/mediadb.db3
	/sbin/start fileindexer
else
	/usr/bin/luna-send -n 1 palm://com.palm.filenotifyd.js/poke '{}'
fi
