#!/bin/bash
#SpagoBI VM verification script

set -e

# log into the VM and check the SpagoBI service
ssh ubuntu@$IP /etc/init.d/spagobi status
if [ "$?" -ne "0" ]; then
  echo "Service is not running"
  exit 1
fi

#check if SpagoBI is running
curl -s -S http://$IP:8080/SpagoBI/servlet/AdapterHTTP?PAGE=LoginPage\&NEW_SESSION=TRUE > /dev/null
