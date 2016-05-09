#
# Cookbook Name:: spagobi
# Recipe:: default
#
#
# All rights reserved - Do Not Redistribute
#

bash "SpagoBI Shutdown" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}bin/" 
  code <<-EOT		
	sh shutdown.sh 		
  EOT
end