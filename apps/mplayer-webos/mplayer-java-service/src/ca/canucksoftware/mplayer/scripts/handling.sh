#!/bin/sh

ACTION=$1

/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/removeHandlersForAppId '{"appId":"ca.canucksoftware.mplayer"}'
if [ "$ACTION" == "enable" ] ; then
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"mpeg", "mimeType":"video/mpeg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"mpg", "mimeType":"video/mpeg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"vob", "mimeType":"video/mpeg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"avi", "mimeType":"video/avi", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"ogg", "mimeType":"video/ogg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"ogv", "mimeType":"video/ogg", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"asf", "mimeType":"video/x-ms-asf", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"wmv", "mimeType":"video/x-ms-wmv", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"qt", "mimeType":"video/quicktime", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"mov", "mimeType":"video/quicktime", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"mp4", "mimeType":"video/quicktime", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"rm", "mimeType":"application/vnd.rn-realmedia", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"rv", "mimeType":"video/vnd.rn-realvideo", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"mkv", "mimeType":"video/x-matroska", "shouldDownload":false}'
	/usr/bin/luna-send -n 1 palm://com.palm.applicationManager/addResourceHandler '{"appId":"ca.canucksoftware.mplayer", "extension":"flv", "mimeType":"video/x-flv", "shouldDownload":false}'
fi
