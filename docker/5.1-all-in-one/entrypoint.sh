#!/bin/bash
set -e

#get the address of container
#example : default via 172.17.42.1 dev eth0 172.17.0.0/16 dev eth0 proto kernel scope link src 172.17.0.109
PUBLIC_ADDRESS=`ip route | grep src | awk '{print $9}'`

#replace the address of container inside server.xml
sed -i "s/http:\/\/.*:8080/http:\/\/${PUBLIC_ADDRESS}:8080/g" ${SPAGOBI_DIRECTORY}/conf/server.xml

exec "$@"