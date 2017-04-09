#!/bin/sh

if [ -d /sys/devices/i2c-4/4-0033/flash/flash0/ ] ; then
	#Pre3 specific led flash controller
	/bin/echo -n 0 > /sys/devices/i2c-4/4-0033/flash/flash0/flash_or_torch_start || true
	/bin/echo -n 0 > /sys/devices/i2c-4/4-0033/flash/flash0/flash_enable || true
	#default to 150mA brightness
	/bin/echo -n 150 > /sys/devices/i2c-4/4-0033/flash/flash0/torch_current || true
else
	/bin/echo -n shutdown > /sys/class/i2c-adapter/i2c-2/2-0033/mode
	/bin/echo -n 0mA > /sys/class/i2c-adapter/i2c-2/2-0033/torch_current
	/bin/echo -n 0 > /sys/class/i2c-adapter/i2c-2/2-0033/avin
fi


