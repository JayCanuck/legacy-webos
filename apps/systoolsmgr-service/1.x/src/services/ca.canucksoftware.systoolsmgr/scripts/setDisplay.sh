#!/bin/sh

STATE=$1

if [ -f /sys/class/display/lcd.0/state ] ; then
	/bin/echo 1 > /sys/class/display/lcd.0/state
fi

/usr/bin/luna-send -n 1 palm://com.palm.display/control/setState '{"state":"$STATE"}'
