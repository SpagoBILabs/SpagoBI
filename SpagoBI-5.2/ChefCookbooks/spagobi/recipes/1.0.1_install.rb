#
# Cookbook Name:: spagobi
# Recipe:: default
#
#
# All rights reserved - Do Not Redistribute
#


####  APT-GET UPDATE FOR DEBIAN OS FAMILY ########
####  WILL BE FIRST ACTION (THEN WE HAVE TO REPEAT INTO BASH!) ########

if !File.directory?("/home/SpagoBI/")

execute "apt-get update" do
    ignore_failure true
    action :nothing
end.run_action(:run) if node['platform_family'] == "debian"


####  ADD CRAN REPO FOR R PACKAGES - START ########

#case node[:platform]
#  when "ubuntu"
#     bash "insert_cran_repo_ubuntu" do
#  	user "root"
#  	code <<-EOT
#		sh -c 'echo "deb http://cran.rstudio.com/bin/linux/ubuntu `grep CODENAME /etc/lsb-release | cut -c 18-`/" >> /etc/apt/sources.list'
#		apt-key adv --keyserver keyserver.ubuntu.com --recv-keys E084DAB9
#  	EOT
#     end
#  when "debian"
#     bash "insert_cran_repo_debian" do
#  	user "root"
#  	code <<-EOS
#		sh -c 'echo "deb http://cran.rstudio.com/bin/linux/debian wheezy-cran3/" >> /etc/apt/sources.list'
#		apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 381BA480
#  	EOS
#     end
#  when "centos"
#     bash "insert_cran_repo_centos" do
#  	user "root"
#  	code <<-EOS
#		su -c 'rpm -Uvh http://download.fedoraproject.org/pub/epel/6/i386/epel-release-6-8.noarch.rpm'
#		sed -i "s/mirrorlist=https/mirrorlist=http/" /etc/yum.repos.d/epel.repo
#		sed -i "2itimeout=180" /etc/yum.conf
#  	EOS
#     end  
#end

####  ADD CRAN REPO FOR R PACKAGES - END ########

####  ADD CRAN REPO FOR R PACKAGES ########


####  INSTALL R PACKAGES - START ########
####  FIRST TO UPDATE APT-GET -> MANDATORY TO MANAGE R -v ########

#case node[:platform_family]
#  when "debian"
#	package "r-base" do
#  		action :install
#	end
#	package "r-base-dev" do
#  		action :install
#	end
#	package "r-recommended" do
#  		action :install
#	end
#	package "libdbd-mysql" do
#  		action :install
#	end
#  when "rhel"
#	package "R" do
#  		action :install
#	end
#end

####  INSTALL R PACKAGES - END ########

####  APT-GET UPDATE FOR DEBIAN OS FAMILY ########
####  MANDATORY TO UPDATE CRAN R REPO ########

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


####  UPGRADE R PACKAGES ########
####  MANDATORY BECAUSE REPO INSTALL R -v 3.0.1 AND TM LIB NEEDS -v > 3.1.0 ########

#case node[:platform_family]
#  when "debian"
#	package "r-base" do
#  		action :upgrade
#	end
#	package "r-base-dev" do
#  		action :upgrade
#	end
#	package "r-recommended" do
#  		action :upgrade
#	end
#  when "rhel"
#	package "R" do
#  		action :upgrade
#	end
#end
####  DOWNLOAD R LIBS NEEDED - START  ########

#directory "#{node[:spagobi][:r_tmp_dir]}" do
#  owner node[:spagobi][:server_user]
#  group node[:spagobi][:server_group]
#  mode 0755
#  action :create
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:dbi_package]}" do
#    source "#{node[:spagobi][:dbi_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:rmysql_package]}" do
#    source "#{node[:spagobi][:rmysql_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:tau_package]}" do
#    source "#{node[:spagobi][:tau_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:NLP_package]}" do
#    source "#{node[:spagobi][:NLP_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:slam_package]}" do
#    source "#{node[:spagobi][:slam_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:tm_package]}" do
#    source "#{node[:spagobi][:tm_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#case node['platform_version']
#when "12.04"
#  remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:codetools_package]}" do
#      source "#{node[:spagobi][:codetools_link]}"
#      mode 0755
#      owner node[:spagobi][:server_user]
#      group node[:spagobi][:server_group]
#      action :create_if_missing
#  end
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:rcpp_package]}" do
#    source "#{node[:spagobi][:rcpp_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:plyr_package]}" do
#    source "#{node[:spagobi][:plyr_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:stringr_package]}" do
#    source "#{node[:spagobi][:stringr_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:reshape2_package]}" do
#    source "#{node[:spagobi][:reshape2_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:modeltools_package]}" do
#    source "#{node[:spagobi][:modeltools_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:gsl_package]}" do
#    source "#{node[:spagobi][:gsl_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:topicmodels_package]}" do
#    source "#{node[:spagobi][:topicmodels_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:pbapply_package]}" do
#    source "#{node[:spagobi][:pbapply_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end
#
#remote_file "#{node[:spagobi][:r_tmp_dir]}#{node[:spagobi][:rjava_package]}" do
#    source "#{node[:spagobi][:rjava_link]}"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end

####  INSTALL R LIBS NEEDED  ########
####DON'T CHANGE THE INSTALLATION ORDER! ######


#case node[:platform_family]
#when "debian"
#   bash "install_r_dbi_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:dbi_package]}
#  	EOS
#   end
#
#   bash "install_r_rmysql_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:rmysql_package]}
#  	EOS
#   end
#
#   bash "install_r_tau_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:tau_package]}
#  	EOS
#   end
#
#   bash "install_r_NLP_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:NLP_package]}
#  	EOS
#   end
#
#   bash "install_r_slam_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:slam_package]}
#  	EOS
#   end
#
#   bash "install_r_tm_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:tm_package]}
#  	EOS
#   end
#
#   #case node['platform_version']
#   #    when "12.04"
#         bash "install_r_codetools_package" do
#  	   user "root"
#	   cwd node[:spagobi][:r_tmp_dir]
#  	   code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:codetools_package]}
#  	   EOS
#       end
#   #end
#
#   bash "install_r_rcpp_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:rcpp_package]}
#  	EOS
#   end
#
#   bash "install_r_plyr_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:plyr_package]}
#  	EOS
#   end
#
#   bash "install_r_stringr_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:stringr_package]}
#  	EOS
#   end
#
#   bash "install_r_reshape2_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:reshape2_package]}
#  	EOS
#   end
#
#   bash "install_r_modeltools_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:modeltools_package]}
#  	EOS
#   end
#
#   bash "install_gsl_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		tar zxf gsl*
#		cd gsl*
#		./configure 
#		make 
#		make install 
#  	EOS
#   end
#
#   bash "export_gsl_lib" do
#  	user "root"
#  	code <<-EOS
#		ldconfig
#  	EOS
#   end
#
#   if platform?("centos")
#	yum_package('gsl') { action :nothing }.run_action(:install)
#   end
#
#
#   bash "install_r_topicmodels_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:topicmodels_package]}
#  	EOS
#   end
#
#   bash "install_r_pbapply_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD INSTALL #{node[:spagobi][:pbapply_package]}
#  	EOS
#   end
#
#   bash "install_rjava_package" do
#  	user "root"
#	cwd node[:spagobi][:r_tmp_dir]
#  	code <<-EOS
#		R CMD javareconf
#		R CMD INSTALL #{node[:spagobi][:rjava_package]}
#  	EOS
#   end
#end


####  SET R_HOME ########
#
#ruby_block  "set-env-r-home" do
#  block do
#    ENV["R_HOME"] = node['spagobi']['r_home']
#  end
#  not_if { ENV["R_HOME"] == node['spagobi']['r_home'] }
#end
#
#directory "/etc/profile.d" do
#  mode 00755
#end
#
#file "/etc/profile.d/R.sh" do
#  content "export R_HOME=#{node['spagobi']['r_home']}"
#  mode 00755
#end
#
#if node['spagobi']['r_set_etc_environment']
#  ruby_block "Set R_HOME in /etc/environment" do
#    block do
#      file = Chef::Util::FileEdit.new("/etc/environment")
#      file.insert_line_if_no_match(/^R_HOME=/, "R_HOME=#{node['spagobi']['r_home']}")
#      file.search_file_replace_line(/^R_HOME=/, "R_HOME=#{node['spagobi']['r_home']}")
#      file.write_file
#    end
#  end
#end



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



# Deploy SpagoBI 
#cookbook_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:file_name]}" do
#    source node[:spagobi][:file_name]
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end

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


template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}conf/server.xml" do
    source "server.xml.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
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


############ OAUTH2 CONF - START #########################


template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/oauth2.config.properties" do    
    source "oauth2/oauth2.config.properties.erb"
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    mode 0755
end


############ OAUTH2 CONF - END #########################


############ CKAN CONF - START #########################

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBI/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIAccessibilityEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIBirtReportEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIChartEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBICockpitEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBICommonJEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIConsoleEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIDataMiningEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIGeoEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIGeoReportEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIJasperReportEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIJPivotEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIMobileEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBINetworkEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIQbeEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBISocialAnalysis/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBITalendEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

#template "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}webapps/SpagoBIWhatIfEngine/WEB-INF/classes/ckan.config.properties" do    
#    source "ckan/ckan.config.properties.erb"
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    mode 0755
#end

############ CKAN CONF - END #########################

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


#file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}lib/mysql-connector-java-5.0.8-bin.jar" do
#    action :delete
#end

#cookbook_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}lib/mysql-connector-java-5.1.33-bin.jar" do
#    source "mysql-connector-java-5.1.33-bin.jar"
#    mode 0755
#    owner node[:spagobi][:server_user]
#    group node[:spagobi][:server_group]
#    action :create_if_missing
#end


bash "make executable" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}" 
  code <<-EOT		
	chmod +x bin/catalina.sh && chmod +x bin/startup.sh && chmod +x database/start.sh 		
  EOT
end


bash "SpagoBI Startup" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}bin" 
  code <<-EOT		
	sh startup.sh 		
  EOT
end

end