
##### SPAGOBI GENERIC ATTRIBUTES #############################################

default["demo"] = false
default["spagobi"]["home_dir"] = "/opt/spagobi/"
default["spagobi"]["server_dir"] = "All-in-One-SpagoBI-5.1-1feb2d97af/"
default["spagobi"]["file_name"] = "All-in-One-SpagoBI-5.1-1feb2d97af.zip"
default["spagobi"]["db_name"] = "spagobi"
default["spagobi"]["db_username"] = "spagobi"
default["spagobi"]["db_password"] = "bispago"
default["spagobi"]["public_ip"] = `dig +short myip.opendns.com @resolver1.opendns.com | tr -d '\n' `
default["spagobi"]["remote_location"] = "https://github.com/SpagoBILabs/SpagoBI/releases/download/fiware-v5.1-1feb2d97af/All-in-One-SpagoBI-5.1-1feb2d97af.zip"
default["spagobi"]["remote_mysql_dbscripts"] = "https://github.com/SpagoBILabs/SpagoBI/releases/download/fiware-v5.1-1feb2d97af/MySQL.zip"
default["spagobi"]["Xms"] = 512
default["spagobi"]["Xmx"] = 1024
default["spagobi"]["MaxPermSize"] = 512
default["spagobi"]["java_security_egd"] = "file:/dev/./urandom"

if node[:demo] == true
	default["spagobi"]["db_dialect"] = "org.hibernate.dialect.HSQLDialect"
	default["spagobi"]["quartz_driver"] = "org.quartz.impl.jdbcjobstore.HSQLDBDelegate"
else
	default["spagobi"]["db_dialect"] = "org.hibernate.dialect.MySQLDialect"
	default["spagobi"]["quartz_driver"] = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"
end


###### OAUTH 2 #################################################

default["spagobi"]["spagobi_sso_class"] = "it.eng.spagobi.services.common.FakeSsoService"

default["spagobi"]["oauth2_client_id"] = "Insert your CLIENT ID"
default["spagobi"]["oauth2_secret"] = "Insert your SECRET"

default["spagobi"]["oauth2_authorize_url"] = "https://account.lab.fiware.org/oauth2/authorize"
default["spagobi"]["oauth2_access_token_url"] = "https://account.lab.fiware.org/oauth2/token"
default["spagobi"]["oauth2_user_info_url"] = "https://account.lab.fiware.org/user"
default["spagobi"]["oauth2_redirect_uri"] = "Insert your REDIRECT URI"

default["spagobi"]["oauth2_rest_base_url"] = "http://cloud.lab.fiware.org:4730/v3/"
default["spagobi"]["oauth2_token_path"] = "auth/tokens"
default["spagobi"]["oauth2_roles_path"] = "OS-ROLES/roles"
default["spagobi"]["oauth2_organizations_list_path"] = "OS-ROLES/organizations/role_assignments"
default["spagobi"]["oauth2_organization_info_path"] = "projects/"

default["spagobi"]["oauth2_admin_id"] = "Insert your ADMIN ID"
default["spagobi"]["oauth2_admin_email"] = "Insert your ADMIN EMAIL"
default["spagobi"]["oauth2_admin_password"] = "Insert your ADMIN PASSWORD"


###### CKAN #################################################

default["spagobi"]["ckan_url"] = "https://data.lab.fiware.org"

##### START R ATTRIBUTES #############################################
##### NOW DISABLED - NO RAM RESOURCES ################################

#default["spagobi"]["r_ubuntu_version"] = "3.1.2-1trusty0"

#default["spagobi"]["r_install"] = true

#default["spagobi"]["r_home"] = "/usr/lib/R"
#default["spagobi"]["r_set_etc_environment"] = false

#default["spagobi"]["r_tmp_dir"] = "/tmp/Rpackages/"
#default["spagobi"]["dbi_package"] = "DBI_0.3.1.tar.gz"
#default["spagobi"]["dbi_link"] = "http://cran.r-project.org/src/contrib/DBI_0.3.1.tar.gz"
#default["spagobi"]["rmysql_package"] = "RMySQL_0.10.2.tar.gz"
#default["spagobi"]["rmysql_link"] = "http://cran.r-project.org/src/contrib/RMySQL_0.10.2.tar.gz"
#default["spagobi"]["tau_package"] = "tau_0.0-18.tar.gz"
#default["spagobi"]["tau_link"] = "http://cran.r-project.org/src/contrib/tau_0.0-18.tar.gz"
#default["spagobi"]["tm_package"] = "tm_0.6.tar.gz"
#default["spagobi"]["tm_link"] = "http://cran.r-project.org/src/contrib/tm_0.6.tar.gz"
#default["spagobi"]["plyr_package"] = "plyr_1.8.1.tar.gz"
#default["spagobi"]["plyr_link"] = "http://cran.r-project.org/src/contrib/plyr_1.8.1.tar.gz"
#default["spagobi"]["NLP_package"] = "NLP_0.1-6.tar.gz"
#default["spagobi"]["NLP_link"] = "http://cran.r-project.org/src/contrib/NLP_0.1-6.tar.gz"
#default["spagobi"]["slam_package"] = "slam_0.1-32.tar.gz"
#default["spagobi"]["slam_link"] = "http://cran.r-project.org/src/contrib/slam_0.1-32.tar.gz"
#default["spagobi"]["rcpp_package"] = "Rcpp_0.11.4.tar.gz"
#default["spagobi"]["rcpp_link"] = "http://cran.r-project.org/src/contrib/Rcpp_0.11.4.tar.gz"
#default["spagobi"]["reshape2_package"] = "reshape2_1.4.1.tar.gz"
#default["spagobi"]["reshape2_link"] = "http://cran.r-project.org/src/contrib/reshape2_1.4.1.tar.gz"
#default["spagobi"]["topicmodels_package"] = "topicmodels_0.2-1.tar.gz"
#default["spagobi"]["topicmodels_link"] = "http://cran.r-project.org/src/contrib/topicmodels_0.2-1.tar.gz"
#default["spagobi"]["stringr_package"] = "stringr_0.6.2.tar.gz"
#default["spagobi"]["stringr_link"] = "http://cran.r-project.org/src/contrib/stringr_0.6.2.tar.gz"
#default["spagobi"]["pbapply_package"] = "pbapply_1.1-1.tar.gz"
#default["spagobi"]["pbapply_link"] = "http://cran.r-project.org/src/contrib/pbapply_1.1-1.tar.gz"
#default["spagobi"]["modeltools_package"] = "modeltools_0.2-21.tar.gz"
#default["spagobi"]["modeltools_link"] = "http://cran.r-project.org/src/contrib/modeltools_0.2-21.tar.gz"
#default["spagobi"]["rjava_package"] = "rJava_0.9-6.tar.gz"
#default["spagobi"]["rjava_link"] = "http://cran.r-project.org/src/contrib/rJava_0.9-6.tar.gz"
#default["spagobi"]["codetools_package"] = "codetools_0.2-10.tar.gz"
#default["spagobi"]["codetools_link"] = "http://cran.r-project.org/src/contrib/Archive/codetools/codetools_0.2-10.tar.gz"


#default["spagobi"]["java_jri_path"] = "/usr/local/lib/R/site-library/rJava/jri"


#default["spagobi"]["gsl_package"] = "gsl-latest.tar.gz"
#default["spagobi"]["gsl_link"] = "ftp://ftp.gnu.org/gnu/gsl/gsl-latest.tar.gz"


##### END R ATTRIBUTES #############################################


##### Social Analysis ATTRIBUTES #############################################

default["spagobi"]["oauth_consumerKey"] = ""
default["spagobi"]["oauth_consumerSecret"] = ""
default["spagobi"]["oauth_accessToken"] = ""
default["spagobi"]["oauth_accessTokenSecret"] = ""
#default["spagobi"]["r_service_address"] = "" 
default["spagobi"]["bitly_access_token"] = "" 


##### SERVER ATTRIBUTES #############################################

default["spagobi"]["server_user"] = "root"
default["spagobi"]["server_group"] = "root"


##### JAVA ATTRIBUTES #############################################

default['java']['jdk_version'] = '7'
default['java']['install_flavor'] = 'oracle'
default['java']['oracle']['accept_oracle_download_terms'] = true

