FROM java:7

ENV SPAGOBI_VERSION=All-In-One-SpagoBI-5.2.1-6030e025b0
ENV SPAGOBI_URL=https://github.com/SpagoBILabs/SpagoBI/releases/download/fiware-v5.2-6030e025b0/${SPAGOBI_VERSION}.zip

RUN apt-get update && apt-get install -y wget coreutils unzip mysql-client

#download spagobi all in one and extract it
WORKDIR /home
RUN wget "${SPAGOBI_URL}" -O SpagoBI.zip && \
	unzip SpagoBI.zip && \
	mv ${SPAGOBI_VERSION} SpagoBI && \
	rm SpagoBI.zip

ENV SPAGOBI_DIRECTORY /home/SpagoBI
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
