#!/bin/sh

APPID=$1
PATH=/media/cryptofs/apps/usr/palm/applications/$APPID

/bin/mv -f $PATH/appinfo.json $PATH/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/appinfo.json.bak > $PATH/appinfo.json

/bin/mv -f $PATH/resources/de/appinfo.json $PATH/resources/de/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/de/appinfo.json.bak > $PATH/resources/de/appinfo.json

/bin/mv -f $PATH/resources/es/appinfo.json $PATH/resources/es/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/es/appinfo.json.bak > $PATH/resources/es/appinfo.json

/bin/mv -f $PATH/resources/fr/appinfo.json $PATH/resources/fr/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/fr/appinfo.json.bak > $PATH/resources/fr/appinfo.json

/bin/mv -f $PATH/resources/it/appinfo.json $PATH/resources/it/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/it/appinfo.json.bak > $PATH/resources/it/appinfo.json

/bin/mv -f $PATH/resources/pl/appinfo.json $PATH/resources/pl/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/pl/appinfo.json.bak > $PATH/resources/pl/appinfo.json

/bin/mv -f $PATH/resources/zh/appinfo.json $PATH/resources/zh/appinfo.json.bak
/bin/sed -e "s|\"visible\": true|\"visible\": false|" $PATH/resources/zh/appinfo.json.bak > $PATH/resources/zh/appinfo.json
