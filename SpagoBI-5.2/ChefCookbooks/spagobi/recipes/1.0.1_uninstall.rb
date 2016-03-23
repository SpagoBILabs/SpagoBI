#
# Cookbook Name:: spagobi
# Recipe:: default
#
#
# All rights reserved - Do Not Redistribute
#


mysql_connection_info = 
{
    :host     => '127.0.0.1',
    :username => 'root',
    :password => 'root'
}


mysql_database node['spagobi']['db_name'] do
    connection mysql_connection_info
    action     :drop
end


directory "#{node[:spagobi][:home_dir]}" do
  owner node[:spagobi][:server_user]
  group node[:spagobi][:server_group]
  mode 0755
  recursive true
  action :delete
end