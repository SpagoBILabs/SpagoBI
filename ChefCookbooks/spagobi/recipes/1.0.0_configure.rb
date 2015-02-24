#
# Cookbook Name:: spagobi
# Recipe:: default
#
# Copyright 2015, SpagoBI Labs
#
# All rights reserved - Do Not Redistribute
#

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

#SOCIAL CONF
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


file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}lib/mysql-connector-java-5.0.8-bin.jar" do
    action :delete
end

cookbook_file "#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}lib/mysql-connector-java-5.1.33-bin.jar" do
    source "mysql-connector-java-5.1.33-bin.jar"
    mode 0755
    owner node[:spagobi][:server_user]
    group node[:spagobi][:server_group]
    action :create_if_missing
end


bash "make executable" do  
  cwd	"#{node[:spagobi][:home_dir]}#{node[:spagobi][:server_dir]}" 
  code <<-EOT		
	chmod +x bin/catalina.sh && chmod +x bin/startup.sh && chmod +x bin/SpagoBIStartup.sh && chmod +x database/start.sh 		
  EOT
end