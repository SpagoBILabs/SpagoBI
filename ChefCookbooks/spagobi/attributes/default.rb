
##### SPAGOBI GENERIC ATTRIBUTES #############################################

default["spagobi"]["home_dir"] = "/home/SpagoBI/"
default["spagobi"]["server_dir"] = "All-In-One-SpagoBI-5.1.0_21012015/"
default["spagobi"]["file_name"] = "All-In-One-SpagoBI-5.1-21012015.zip"
default["spagobi"]["db_name"] = "spagobi"
default["spagobi"]["db_username"] = "spagobi"
default["spagobi"]["db_password"] = "bispago"
#default["spagobi"]["public_ip"] = `curl ifconfig.me | tr -d '\n' `
default["spagobi"]["public_ip"] = `dig +short myip.opendns.com @resolver1.opendns.com | tr -d '\n' `
default["spagobi"]["remote_location"] = "http://download.forge.ow2.org/spagobi/All-In-One-SpagoBI-5.1-21012015.zip"
default["spagobi"]["remote_mysql_dbscripts"] = "http://download.forge.ow2.org/spagobi/mysql-dbscript-5.1.0_19012015.zip"
default["spagobi"]["Xms"] = 512
default["spagobi"]["Xmx"] = 1024
default["spagobi"]["MaxPermSize"] = 1024
default["spagobi"]["java_security_egd"] = "file:/dev/./urandom"
default["spagobi"]["db_dialect"] = "org.hibernate.dialect.MySQLDialect"
default["spagobi"]["quartz_driver"] = "org.quartz.impl.jdbcjobstore.StdJDBCDelegate"


##### R ATTRIBUTES #############################################

default["spagobi"]["r_ubuntu_version"] = "3.1.2-1trusty0"

default["spagobi"]["r_install"] = true

default["spagobi"]["r_home"] = "/usr/lib/R"
default["spagobi"]["r_set_etc_environment"] = false

default["spagobi"]["r_tmp_dir"] = "/tmp/Rpackages/"
default["spagobi"]["dbi_package"] = "DBI_0.3.1.tar.gz"
default["spagobi"]["dbi_link"] = "http://cran.r-project.org/src/contrib/DBI_0.3.1.tar.gz"
default["spagobi"]["rmysql_package"] = "RMySQL_0.10.1.tar.gz"
default["spagobi"]["rmysql_link"] = "http://cran.r-project.org/src/contrib/RMySQL_0.10.1.tar.gz"
default["spagobi"]["tau_package"] = "tau_0.0-18.tar.gz"
default["spagobi"]["tau_link"] = "http://cran.r-project.org/src/contrib/tau_0.0-18.tar.gz"
default["spagobi"]["tm_package"] = "tm_0.6.tar.gz"
default["spagobi"]["tm_link"] = "http://cran.r-project.org/src/contrib/tm_0.6.tar.gz"
default["spagobi"]["plyr_package"] = "plyr_1.8.1.tar.gz"
default["spagobi"]["plyr_link"] = "http://cran.r-project.org/src/contrib/plyr_1.8.1.tar.gz"
default["spagobi"]["NLP_package"] = "NLP_0.1-6.tar.gz"
default["spagobi"]["NLP_link"] = "http://cran.r-project.org/src/contrib/NLP_0.1-6.tar.gz"
default["spagobi"]["slam_package"] = "slam_0.1-32.tar.gz"
default["spagobi"]["slam_link"] = "http://cran.r-project.org/src/contrib/slam_0.1-32.tar.gz"
default["spagobi"]["rcpp_package"] = "Rcpp_0.11.4.tar.gz"
default["spagobi"]["rcpp_link"] = "http://cran.r-project.org/src/contrib/Rcpp_0.11.4.tar.gz"
default["spagobi"]["reshape2_package"] = "reshape2_1.4.1.tar.gz"
default["spagobi"]["reshape2_link"] = "http://cran.r-project.org/src/contrib/reshape2_1.4.1.tar.gz"
default["spagobi"]["topicmodels_package"] = "topicmodels_0.2-1.tar.gz"
default["spagobi"]["topicmodels_link"] = "http://cran.r-project.org/src/contrib/topicmodels_0.2-1.tar.gz"
default["spagobi"]["stringr_package"] = "stringr_0.6.2.tar.gz"
default["spagobi"]["stringr_link"] = "http://cran.r-project.org/src/contrib/stringr_0.6.2.tar.gz"
default["spagobi"]["pbapply_package"] = "pbapply_1.1-1.tar.gz"
default["spagobi"]["pbapply_link"] = "http://cran.r-project.org/src/contrib/pbapply_1.1-1.tar.gz"
default["spagobi"]["modeltools_package"] = "modeltools_0.2-21.tar.gz"
default["spagobi"]["modeltools_link"] = "http://cran.r-project.org/src/contrib/modeltools_0.2-21.tar.gz"
default["spagobi"]["rjava_package"] = "rJava_0.9-6.tar.gz"
default["spagobi"]["rjava_link"] = "http://cran.r-project.org/src/contrib/rJava_0.9-6.tar.gz"
default["spagobi"]["codetools_package"] = "codetools_0.2-10.tar.gz"
default["spagobi"]["codetools_link"] = "http://cran.r-project.org/src/contrib/codetools_0.2-10.tar.gz"


default["spagobi"]["java_jri_path"] = "/usr/local/lib/R/site-library/rJava/jri"


default["spagobi"]["gsl_package"] = "gsl-latest.tar.gz"
default["spagobi"]["gsl_link"] = "ftp://ftp.gnu.org/gnu/gsl/gsl-latest.tar.gz"




##### Social Analysis ATTRIBUTES #############################################

default["spagobi"]["oauth_consumerKey"] = "r0BRbAild34uWwx9WCsZI5NFL"
default["spagobi"]["oauth_consumerSecret"] = "FGOwEpGMnsr0hU7wngk78M9d3WFP1xZLEd3daJ5ZWSjyi8XnDI"
default["spagobi"]["oauth_accessToken"] = "361390771-o2I1YavI9KFKugOuk88kZgdvV5Ezh3YtNNrQ4TmS"
default["spagobi"]["oauth_accessTokenSecret"] = "qQxPeFQn8tckqhqlAlIMq3HbCE3cKykAC6RPbBiM3XUMP"
#default["spagobi"]["r_service_address"] = "" 
default["spagobi"]["bitly_access_token"] = "d32762c9990acba4b3f0bd2649d4cdef296941ae" 


##### SERVER ATTRIBUTES #############################################

default["spagobi"]["server_user"] = "root"
default["spagobi"]["server_group"] = "root"


##### JAVA ATTRIBUTES #############################################

default['java']['jdk_version'] = '7'
default['java']['install_flavor'] = 'oracle'
default['java']['oracle']['accept_oracle_download_terms'] = true

