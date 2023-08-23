FROM openjdk:8-jdk-alpine
MAINTAINER pysga1996
RUN apk update
RUN apk --no-cache add curl
WORKDIR /app
COPY ./build/libs/alpha-sound-service-0.0.1-SNAPSHOT.jar /opt
ENTRYPOINT ["/usr/bin/java"]
CMD ["-Dspring.profiles.active=k8s", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5010" , "/opt/alpha-sound-service-0.0.1-SNAPSHOT.jar"]
HEALTHCHECK --start-period=60s --interval=30s --timeout=10s \
  CMD curl -f http://localhost:80/alpha-sound-service/actuator/health | grep UP || exit 1
VOLUME /app
EXPOSE 80