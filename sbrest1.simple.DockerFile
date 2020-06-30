FROM openjdk:8-jre-alpine
MAINTAINER akumar
EXPOSE 8090
COPY target/sts-sb-rest1-0.0.1-SNAPSHOT.jar /app/sb-rest1.jar
ENTRYPOINT [ "java", "-jar", "/app/sb-rest1.jar" ]