FROM java:7

ENV SPAGOBI_VERSION=5.1.0-r24688
ENV SPAGOBI_URL=http://download.forge.ow2.org/spagobi/All-In-One-SpagoBI-${SPAGOBI_VERSION}.zip
ENV SPAGOBI_MYSQL_SCRIPT_URL=http://download.forge.ow2.org/spagobi/mysql-dbscript-5.1.0_19012015.zip

RUN apt-get update && apt-get install -y wget coreutils unzip mysql-client

#download spagobi all in one and extract it
WORKDIR /home
RUN wget "${SPAGOBI_URL}" -O SpagoBI.zip && \
	unzip SpagoBI.zip && \
	mv All-In-One-SpagoBI-${SPAGOBI_VERSION} SpagoBI && \
	rm SpagoBI.zip

#download mysql scripts
RUN wget "${SPAGOBI_MYSQL_SCRIPT_URL}" -O MySQL.zip && \
	unzip MySQL.zip -d MySQL && \
	rm MySQL.zip

ENV SPAGOBI_DIRECTORY /home/SpagoBI
ENV MYSQL_SCRIPT_DIRECTORY /home/MySQL
WORKDIR ${SPAGOBI_DIRECTORY}/bin

#make the script executable by bash (not only sh) and
#make spagobi running forever without exiting
RUN sed -i "s/bin\/sh/bin\/bash/" startup.sh && \
	sed -i "s/EXECUTABLE\" start/EXECUTABLE\" run/" startup.sh

COPY ./entrypoint.sh ./
#make all scripts executable
RUN chmod +x *.sh

#safe copy of original conf files
RUN cp ${SPAGOBI_DIRECTORY}/conf/server.xml ${SPAGOBI_DIRECTORY}/conf/server.xml.bak && \
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/hibernate.cfg.xml.bak && \
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/jbpm.hibernate.cfg.xml.bak && \
	cp ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/quartz.properties ${SPAGOBI_DIRECTORY}/webapps/SpagoBI/WEB-INF/classes/quartz.properties.bak

#where the data is stored in all in one run
VOLUME ${SPAGOBI_DIRECTORY}/database

EXPOSE 8080
#-d option is passed to run spagobi forever without exiting from container
ENTRYPOINT ["./entrypoint.sh"]
CMD ["./startup.sh"]




