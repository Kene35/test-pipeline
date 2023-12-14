FROM nsos-pr1-nex.atldc.nscorp.com:8083/ubi8/openjdk-17-runtime
#USER root
#RUN mkdir -p /haha
VOLUME /tmp
COPY *.jar ./app.jar
EXPOSE 8099
ENTRYPOINT java -jar app.jar
