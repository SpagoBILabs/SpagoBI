#!/bin/bash
set -e
set -x

#install chef 12 client
if [ ! -d "/opt/chef" ]; then
	wget https://opscode-omnibus-packages.s3.amazonaws.com/ubuntu/10.04/x86_64/chef_12.5.1-1_amd64.deb
	sudo dpkg -i chef_12.5.1-1_amd64.deb
	rm -f chef_12.5.1-1_amd64.deb
fi

#download recipes
sudo apt-get update
sudo apt-get -y install subversion
rm -rf cookbooks
svn checkout https://github.com/SpagoBILabs/SpagoBI/trunk/ChefCookbooks/ cookbooks


#install spagobi
if [ "$1" == "demo" ]; then
	echo '{"demo":true}' | sudo chef-client -z -o 'recipe[spagobi::1.0.2_install]' -j /dev/stdin
else
	sudo chef-client -z -o 'recipe[spagobi::1.0.2_install]'
fi

#remove recipes
rm -rf cookbooks
