#!/bin/bash
set -e

if [[ -z "$PUBLIC_ADDRESS" ]]; then
	#get the address of container
	#example : default via 172.17.42.1 dev eth0 172.17.0.0/16 dev eth0 proto kernel scope link src 172.17.0.109
	PUBLIC_ADDRESS=`ip route | grep src | awk '{print $9}'`
fi


#replace the address of container inside server.xml
sed -i "s/http:\/\/.*:8080/http:\/\/${PUBLIC_ADDRESS}:8080/g" ${SPAGOBI_DIRECTORY}/conf/server.xml
sed -i "s/http:\/\/192\.168\.93\.1:8080/http:\/\/${PUBLIC_ADDRESS}:8080/g" ${SPAGOBI_DIRECTORY}/webapps/SpagoBIConsoleEngine/WEB-INF/web.xml
sed -i "s/http:\/\/192\.168\.93\.1:8080/http:\/\/${PUBLIC_ADDRESS}:8080/g" ${SPAGOBI_DIRECTORY}/webapps/SpagoBIChartEngine/WEB-INF/web.xml

#wait for MySql if it's a compose image
if [ -n "$WAIT_MYSQL" ]; then
	while ! curl http://$DB_PORT_3306_TCP_ADDR:$DB_PORT_3306_TCP_PORT/
	do
	  echo "$(date) - still trying to connect to mysql"
	  sleep 1
	done
fi

if [ -n "$DB_ENV_MYSQL_DATABASE" ]; then
	#copy the original files
	cp ${SPAGOBI_DIRECTORY}/conf/server.xml.bak ${SPAGOBI_DIRECTORY}/conf/server.xml
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml.bak ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml.bak ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/quartz.properties.bak ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/quartz.properties

	# Get the database values from the relation.
	DB_USER=$DB_ENV_MYSQL_USER
	DB_DB=$DB_ENV_MYSQL_DATABASE
	DB_PASS=$DB_ENV_MYSQL_PASSWORD
	DB_HOST=$DB_PORT_3306_TCP_ADDR
	DB_PORT=$DB_PORT_3306_TCP_PORT

	#replace hsql with mysql
	#replace in server.xml
	old_driver='org\.hsqldb\.jdbcDriver'
	new_driver='com\.mysql\.jdbc\.Driver'
	sed -i "s|${old_driver}|${new_driver}|" ${SPAGOBI_DIRECTORY}/conf/server.xml
	old_connection='jdbc:hsqldb:file:${catalina.base}/database/spagobi'
	mysql_connection='jdbc:mysql://'${DB_HOST}':'${DB_PORT}'/'${DB_DB}
	sed -i "s|${old_connection}|${mysql_connection}|" ${SPAGOBI_DIRECTORY}/conf/server.xml
	old_username_password='username="sa" password=""'
	new_username_password='username="'${DB_USER}'" password="'${DB_PASS}'"'
	sed -i "s|${old_username_password}|${new_username_password}|" ${SPAGOBI_DIRECTORY}/conf/server.xml
	#replace in properties files dialect
	old_dialect='org\.hibernate\.dialect\.HSQLDialect'
	new_dialect='org\.hibernate\.dialect\.MySQLDialect'
	sed -i "s|${old_dialect}|${new_dialect}|" ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml
	sed -i "s|${old_dialect}|${new_dialect}|" ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml
	#replace in properties files delegate
	old_delegate='org\.quartz\.impl\.jdbcjobstore\.HSQLDBDelegate'
	new_delegate='org\.quartz\.impl\.jdbcjobstore\.StdJDBCDelegate'
	sed -i "s|${old_delegate}|${new_delegate}|" ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/quartz.properties


fi

exec "$@"
