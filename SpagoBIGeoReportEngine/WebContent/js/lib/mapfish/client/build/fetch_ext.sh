#!/bin/sh
#
# This script is used to fetch the Ext library into MapFish/client/mfbase/ext
# Ext sources are then commited to svn, so this script is only needed when updating ext.

EXT_VER=ext-2.0.2
EXT_URL=http://extjs.com/deploy/$EXT_VER.zip

set -e

rm -rf ${EXT_VER}* >/dev/null 2>&1 || :

# define EXT_LOCATION in your environment to avoid fetching zip when testing
if test -n "$EXT_LOCATION"; then
	cp $EXT_LOCATION .
else
	echo wget $EXT_URL
fi

unzip -q $EXT_VER.zip

find ../mfbase/ext -type f|grep -v .svn|xargs --no-run-if-empty rm

cp -r $EXT_VER/ext-* ../mfbase/ext
cp -r $EXT_VER/resources/ ../mfbase/ext
cp -r $EXT_VER/adapter/ ../mfbase/ext
cp -r $EXT_VER/source/ ../mfbase/ext
cp -r $EXT_VER/air/ ../mfbase/ext

(cd ../mfbase/ext; svn status|grep "^\!"|sed "s/^\!//"|xargs -n1 --no-run-if-empty svn remove)
(cd ../mfbase/ext; svn status|grep "^\?"|sed "s/^\?//"|xargs -n1 --no-run-if-empty svn add)

rm -rf ${EXT_VER}* >/dev/null 2>&1 || :

