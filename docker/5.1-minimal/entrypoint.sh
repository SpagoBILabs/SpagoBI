#!/bin/bash
set -e


if [ "$1" = "catalina.sh" ] && [ "$2" = "run" ]; then
	# Get the database values from the relation.
	DB_USER=$DB_ENV_MYSQL_USER
	DB_DB=$DB_ENV_MYSQL_DATABASE
	DB_PASS=$DB_ENV_MYSQL_PASSWORD
	DB_HOST=$DB_PORT_3306_TCP_ADDR
	DB_PORT=$DB_PORT_3306_TCP_PORT

	#insert spago bi metadata into db if it doesn't exist
	Result=`mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} ${DB_DB} -e "SHOW TABLES LIKE '%SBI_%';"`
	if [ -z "$Result" ]; then
		mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} ${DB_DB} --execute="source $SPAGOBI_DIRECTORY/scripts/MySQL_create.sql"
		mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} ${DB_DB} --execute="source $SPAGOBI_DIRECTORY/scripts/MySQL_create_quartz_schema.sql"
		mysql -h${DB_HOST} -P${DB_PORT} -u${DB_USER} -p${DB_PASS} ${DB_DB} --execute="INSERT INTO hibernate_sequences(SEQUENCE_NAME,NEXT_VAL) VALUES('SBI_DATA_SOURCE',1);"
	fi

	#insert spagobi main datasource
	DB_RESOURCE='<Resource auth="Container" driverClassName="com.mysql.jdbc.Driver" maxActive="20" maxIdle="10" maxWait="-1" name="jdbc/spagobi" password="'${DB_PASS}'" type="javax.sql.DataSource" url="jdbc:mysql://'${DB_HOST}':'${DB_PORT}'/'${DB_DB}'" username="'${DB_USER}'"/>'

	#example : default via 172.17.42.1 dev eth0 172.17.0.0/16 dev eth0 proto kernel scope link src 172.17.0.109
	PUBLIC_ADDRESS=`ip route | grep src | awk '{print $9}'`
	#Fixed
	PUBLIC_PORT=8080

	#insert environment resources
	spagobi_host='<Environment name="spagobi_host_url" type="java.lang.String" value="http://'${PUBLIC_ADDRESS}':'${PUBLIC_PORT}'"/>'
	spagobi_service='<Environment name="spagobi_service_url" type="java.lang.String" value="http://'${PUBLIC_ADDRESS}':'${PUBLIC_PORT}'/SpagoBI"/>'
	spagobi_resources='<Environment name="spagobi_resource_path" type="java.lang.String" value="${catalina.base}/resources"/>'

	sed -i "/ <GlobalNamingResources>/a \  ${spagobi_host}" ${CATALINA_HOME}/conf/server.xml
	sed -i "/ <GlobalNamingResources>/a \  ${spagobi_service}" ${CATALINA_HOME}/conf/server.xml
	sed -i '/ <GlobalNamingResources>/a \  <Environment name="spagobi_sso_class" type="java.lang.String" value="it.eng.spagobi.services.common.FakeSsoService"/>' ${CATALINA_HOME}/conf/server.xml
	sed -i "/ <GlobalNamingResources>/a \  ${spagobi_resources}" ${CATALINA_HOME}/conf/server.xml
	sed -i "/ <GlobalNamingResources>/a \  ${DB_RESOURCE}" ${CATALINA_HOME}/conf/server.xml
fi

exec "$@"