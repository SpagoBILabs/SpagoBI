#!/bin/bash
#SpagoBI VM installation script

#install chef 12 client
wget https://opscode-omnibus-packages.s3.amazonaws.com/ubuntu/10.04/x86_64/chef_12.5.1-1_amd64.deb
dpkg -i chef_12.5.1-1_amd64.deb
rm -f chef_12.5.1-1_amd64.deb

#download recipes
wget https://github.com/SpagoBILabs/SpagoBI/releases/download/fiware-v5.2-6030e025b0/chef-cookbooks.zip

#install zip
apt-get install unzip

unzip chef-cookbooks.zip

#install spagobi
mkdir /etc/chef
chef-client -z -o 'recipe[spagobi::1.0.3_install]'
rm -rf cookbooks
