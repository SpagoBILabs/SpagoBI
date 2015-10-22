#
# Cookbook Name:: spagobi
# Recipe:: default
#
#
# All rights reserved - Do Not Redistribute
#


####  APT-GET UPDATE FOR DEBIAN OS FAMILY ########
####  WILL BE FIRST ACTION (THEN WE HAVE TO REPEAT INTO BASH!) ########

#uninstall  e delete previous installation to reach idempotency
service "spagobi" do
  action [:stop,:disable]
end

directory "#{node[:spagobi][:home_dir]}" do
  owner node[:spagobi][:server_user]
  group node[:spagobi][:server_group]
  mode 0755
  recursive true
  action :delete
end

file "/etc/init.d/spagobi" do
  owner "root"
  group "root"
  mode 0755
  action :delete
end


execute "apt-get update" do
    ignore_failure true
    action :nothing
end.run_action(:run) if node['platform_family'] == "debian"


case node[:platform_family]
 when "debian"
   bash "apt-get-update" do
  	user "root"
  	code <<-EOS
		apt-get update
  	EOS
   end
end 




node.set['build-essential']['compile_time'] = true
include_recipe "build-essential"

if platform?("debian", "ubuntu")
    package('libmysqlclient-dev') { action :nothing }.run_action(:install)		
else
    package('mysql-devel') { action :nothing }.run_action(:install)
end

#TO SOLVE MYSQL55 PROBLEMS WITH CENTOS
if platform?("centos")
   yum_package('postfix') { action :nothing }.run_action(:remove)
end

chef_gem "mysql"
require "mysql"

package "unzip" do
  action :install
end

include_recipe 'java'

mysql_service 'default1' do
  version '5.5'
  bind_address '0.0.0.0'
  port '3306'
  initial_root_password 'root'
  action [:create, :start]
end

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

# Create a user name
mysql_database_user node[:spagobi][:db_username] do
    connection mysql_connection_info
    password   node[:spagobi][:db_password]
    action     :create
end

# Grant all privileges to all tables in foo db from all hosts
mysql_database_user node[:spagobi][:db_username] do
    connection    mysql_connection_info
    password      node[:spagobi][:db_password] 
    database_name node[:spagobi][:db_name]
    privileges    [:all]
    host '%'
    action        :grant
end


remote_file "/tmp/mysql-dbscript.zip" do
  source "#{node[:spagobi][:remote_mysql_dbscripts]}"
end

execute "unzip-MysqlDbScripts" do
    cwd "/tmp/"
    command "unzip -o mysql-dbscript.zip -d mysql-dbscript/"
end

cookbook_file "/tmp/mysql-dbscript/alter_dataset.sql" do
    source "alter_dataset.sql"
    mode 0755
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    action :create_if_missing
end

# Init DB schema
mysql_database 'populate_spagobi_db' do
    connection mysql_connection_info
    database_name node[:spagobi][:db_name]
    sql { ::File.open('/tmp/mysql-dbscript/MySQL_create.sql').read }
    action :nothing
end

mysql_database 'populate_spagobi_quartz_db' do
    connection mysql_connection_info
    database_name node[:spagobi][:db_name]
    sql { ::File.open('/tmp/mysql-dbscript/MySQL_create_quartz_schema.sql').read }
    action :nothing
end

mysql_database 'populate_spagobi_social_db' do
    connection mysql_connection_info
    database_name node[:spagobi][:db_name]
    sql { ::File.open('/tmp/mysql-dbscript/MySQL_create_social.sql').read }
    action :nothing
end

mysql_database 'modify_dataset_descr' do
    connection mysql_connection_info
    database_name node[:spagobi][:db_name]
    sql { ::File.open('/tmp/mysql-dbscript/alter_dataset.sql').read }
    action :nothing
end

# Create a mysql database
mysql_database node[:spagobi][:db_name] do
    connection mysql_connection_info
    action :create
    notifies :query, 'mysql_database[populate_spagobi_db]', :immediately
    notifies :query, 'mysql_database[populate_spagobi_quartz_db]', :immediately
    notifies :query, 'mysql_database[populate_spagobi_social_db]', :immediately
    notifies :query, 'mysql_database[modify_dataset_descr]', :immediately
end

mysql_database 'flush the privileges' do
    connection mysql_connection_info
    sql        'flush privileges'
    action     :query
end




directory "#{node[:spagobi][:home_dir]}" do
  owner node[:spagobi][:server_user]
  group node[:spagobi][:server_group]
  mode 0755
  recursive true
  action :delete
end

directory "#{node[:spagobi][:home_dir]}" do
  owner node[:spagobi][:server_user]
  group node[:spagobi][:server_group]
  mode 0755
  action :create
end



remote_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:file_name]}" do
    source "#{node[:spagobi][:remote_location]}"
    mode 0755
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    action :create_if_missing
end


execute "unzip-SpagoBI" do
    cwd node[:spagobi][:home_dir]
    command "unzip -o #{node[:spagobi][:file_name]}"
end

file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:file_name]}" do
    action :delete
end

if node[:demo] == true
    template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}conf/server.xml" do
        source "server-demo.xml.erb"
        owner node[:spagobi][:server_user]
        group node[:spagobi][:server_group]
        mode 0755
    end
else
    template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}conf/server.xml" do
        source "server.xml.erb"
        owner node[:spagobi][:server_user]
        group node[:spagobi][:server_group]
        mode 0755
    end
end


template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}bin/setenv.sh" do
    source "setenv.sh.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

#HIBERNATE CONF
template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml" do
    source "sbi_hibernate.cfg.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

#META DB HIBERNATE CONF
template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml" do    
    source "jbpm.hibernate.cfg.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

#QUARTZ PROPERTIES
template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/quartz.properties" do    
    source "quartz.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end


#Console Engine
template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIConsoleEngine/WEB-INF/web.xml" do    
    source "console_engine/web.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

#Chart Engine
template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIChartEngine/WEB-INF/web.xml" do    
    source "chart_engine/web.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end


############ OAUTH2 CONF - START #########################


template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/oauth2.config.properties" do    
    source "oauth2/oauth2.config.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end


############ OAUTH2 CONF - END #########################


############ SOCIAL CONF - START #####################

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBISocialAnalysis/WEB-INF/classes/hibernate.cfg.xml" do    
    source "social/sbi_social_hibernate.cfg.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBISocialAnalysis/WEB-INF/classes/twitter4j.properties" do    
    source "social/twitter4j.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBISocialAnalysis/WEB-INF/classes/bitly.properties" do    
    source "social/bitly.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBISocialAnalysis/WEB-INF/classes/rservices.properties" do    
    source "social/rservices.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}resources/datamining/external/Sentiment_Analysis_Twitter_DB_unificato.r" do    
    source "social/Sentiment_Analysis_Twitter_DB_unificato.r.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}resources/datamining/external/Topic_Modeling_Twitter_TM_outputDB_unificato.r" do    
    source "social/Topic_Modeling_Twitter_TM_outputDB_unificato.r.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end

cookbook_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}resources/datamining/external/TerminiNeg.Rda" do
    source "TerminiNeg.Rda"
    mode 0755
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    action :create_if_missing
end

cookbook_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}resources/datamining/external/TerminiPos.Rda" do
    source "TerminiPos.Rda"
    mode 0755
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    action :create_if_missing
end

############ SOCIAL CONF - END #####################


bash "make executable" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}" 
  code <<-EOT		
	chmod +x bin/catalina.sh && chmod +x bin/startup.sh && chmod +x database/start.sh 		
  EOT
end

template "/etc/init.d/spagobi" do
  source "spagobi.erb"
  owner "root" #must be root
  group "root" #must be root
  mode 0755
end

service "spagobi" do
  supports :restart => true, :start => true, :stop => true, :reload => true, :status => true
  action [:enable,:start]
end



