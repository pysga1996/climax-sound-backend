FROM alpine-java:base
MAINTAINER pysga1996
WORKDIR /opt/alpha-sound-service
COPY ./alpha-sound-service-0.0.1-SNAPSHOT.jar /opt/alpha-sound-service
ENTRYPOINT ["/usr/bin/java"]
CMD ["-Dspring.profiles.active=poweredge", "-jar", "./alpha-sound-service-0.0.1-SNAPSHOT.jar"]
VOLUME /opt/alpha-sound-server
EXPOSE 8081