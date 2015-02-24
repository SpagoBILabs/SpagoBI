#
# Cookbook Name:: spagobi
# Recipe:: default
#
# Copyright 2015, SpagoBI Labs
#
# All rights reserved - Do Not Redistribute
#

bash "SpagoBI Startup" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}bin" 
  code <<-EOT		
	sh SpagoBIStartup.sh 		
  EOT
end