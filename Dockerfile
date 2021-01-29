# Build the jar
FROM maven:latest AS maven

COPY pom.xml /tmp/

WORKDIR tmp
RUN mvn verify clean --fail-never

COPY src /tmp/src/

RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true

# Copy the jar and build image
FROM adoptopenjdk/openjdk11:alpine-jre
RUN mkdir /app

COPY --from=maven /tmp/target/*.jar /app/app.jar

WORKDIR /app/

ENTRYPOINT ["java","-jar","app.jar"]
