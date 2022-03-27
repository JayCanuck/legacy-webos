#!/bin/sh

BRIGHTNESS=$1

if [ -d /sys/devices/i2c-4/4-0033/flash/flash0/ ] ; then
	#Pre3 specific led flash controller
	/bin/echo -n ${BRIGHTNESS} > /sys/devices/i2c-4/4-0033/flash/flash0/torch_current || true
	/bin/echo -n 2 > /sys/devices/i2c-4/4-0033/flash/flash0/flash_enable || true
	/bin/echo -n 1 > /sys/devices/i2c-4/4-0033/flash/flash0/flash_or_torch_start || true
else
	/bin/echo -n 1 > /sys/class/i2c-adapter/i2c-2/2-0033/avin
	/bin/echo -n ${BRIGHTNESS}mA > /sys/class/i2c-adapter/i2c-2/2-0033/torch_current
	/bin/echo -n torch > /sys/class/i2c-adapter/i2c-2/2-0033/mode
fi
