#!/bin/bash
#SpagoBI Chef installation script

#install chef 12 client
wget https://opscode-omnibus-packages.s3.amazonaws.com/ubuntu/10.04/x86_64/chef_12.5.1-1_amd64.deb
sudo dpkg -i chef_12.5.1-1_amd64.deb
rm -f chef_12.5.1-1_amd64.deb

#download recipes
sudo apt-get update -q
sudo apt-get -y install unzip
wget https://github.com/SpagoBILabs/SpagoBI/releases/download/fiware-v5.1-1feb2d97af/chef-cookbooks.zip
unzip chef-cookbooks.zip
rm -f chef-cookbooks.zip

#install spagobi
sudo chef-client -z -o 'recipe[spagobi::1.0.2_install]'
rm -rf cookbooks
