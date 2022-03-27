#!/bin/sh

VIEWSOURCE=$1
IMAGES=$2
TEXT=$3
IPK=$4

/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/removeHandlersForAppId '{"appId":"ca.canucksoftware.internalz"}'
if [ "$VIEWSOURCE" == "enable" ] ; then
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addRedirectHandler '{"appId":"ca.canucksoftware.internalz", "urlPattern":"^view-source://", "schemeForm":true}'
fi
if [ "$IMAGES" == "enable" ] ; then
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"jpg", "mimeType":"image/jpeg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"jpeg", "mimeType":"image/jpeg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"png", "mimeType":"image/png", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"bmp", "mimeType":"image/bmp", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"gif", "mimeType":"image/gif", "shouldDownload":false}'
fi
if [ "$TEXT" == "enable" ] ; then
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"sh", "mimeType":"application/x-sh", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"mk", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"js", "mimeType":"text/javascript", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"css", "mimeType":"text/css", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"json", "mimeType":"application/json", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"txt", "mimeType":"application/txt", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"text.plain", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"log", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"conf", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"ini", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"c", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"cpp", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"cs", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"vb", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"h", "mimeType":"text/plain", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"patch", "mimeType":"text/x-patch", "shouldDownload":true}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"diff", "mimeType":"text/x-diff", "shouldDownload":true}'
fi
if [ "$IPK" == "enable" ] ; then
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.internalz", "extension":"ipk", "mimeType":"application/vnd.webos.ipk"}'
fi
