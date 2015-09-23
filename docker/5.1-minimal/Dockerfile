FROM tomcat:7.0

RUN apt-get update && apt-get install -y wget coreutils unzip mysql-client

ENV SPAGOBI_URL=http://download.forge.ow2.org/spagobi/SpagoBI.zip

#download spago bi and extract it
WORKDIR /home
RUN wget "${SPAGOBI_URL}" -O SpagoBI.zip && \
	unzip SpagoBI.zip && \
	rm SpagoBI.zip

ENV SPAGOBI_DIRECTORY /home/SpagoBI

#copy webapps and resources into tomcat
WORKDIR ${SPAGOBI_DIRECTORY}
RUN cp -r resources $CATALINA_HOME && \
	cp lib/* $CATALINA_HOME/lib && \
	rm -rf $CATALINA_HOME/webapps/* && \
	cp -r apps/* $CATALINA_HOME/webapps/ && \
	rm -rf apps lib resources

WORKDIR $CATALINA_HOME
COPY entrypoint.sh ./
RUN chmod +x entrypoint.sh
ENTRYPOINT ["./entrypoint.sh"]

CMD ["catalina.sh", "run"]



