FROM java:7

ENV SPAGOBI_VERSION=5.1.0-r24688
ENV SPAGOBI_URL=http://download.forge.ow2.org/spagobi/All-In-One-SpagoBI-${SPAGOBI_VERSION}.zip
ENV SPAGOBI_SHA=d74dcac7341a488822e85f6dd0a4f6f641ad07c41b1d834d55dc9bcc353c6576

#download spagobi all in one and extract it
WORKDIR /home
RUN wget "${SPAGOBI_URL}" -O SpagoBI.zip && \
	echo "${SPAGOBI_SHA}  SpagoBI.zip" | sha256sum -c - && \
	unzip SpagoBI.zip && \
	mv All-In-One-SpagoBI-${SPAGOBI_VERSION} SpagoBI && \
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

#where the data is stored
VOLUME ${SPAGOBI_DIRECTORY}/database

EXPOSE 8080
#-d option is passed to run spagobi forever without exiting from container
ENTRYPOINT ["./entrypoint.sh"]
CMD ["./startup.sh"]




