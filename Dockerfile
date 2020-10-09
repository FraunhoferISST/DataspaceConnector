FROM openjdk:11-jdk-slim
FROM maven:latest
LABEL maintainer="Julia Pampus <julia.pampus@isst.fraunhofer.de>"

COPY pom.xml /tmp/
COPY src /tmp/src/

WORKDIR tmp
RUN mvn clean package

WORKDIR target
RUN cp *.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
