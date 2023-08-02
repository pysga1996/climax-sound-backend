FROM openjdk:8-jdk-alpine
MAINTAINER pysga1996
WORKDIR /app
COPY ./build/libs/alpha-sound-service-0.0.1-SNAPSHOT.jar /opt
ENTRYPOINT ["/usr/bin/java"]
CMD ["-Dspring.profiles.active=k8s", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5010" , "/opt/alpha-sound-service-0.0.1-SNAPSHOT.jar"]
VOLUME /app
EXPOSE 80