FROM maven:latest
LABEL maintainer="Julia Pampus <julia.pampus@isst.fraunhofer.de>"

COPY pom.xml /tmp/

WORKDIR tmp
RUN mvn verify clean --fail-never

COPY src /tmp/src/

RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true

WORKDIR target
RUN cp *.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
