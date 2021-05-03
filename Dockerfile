# Dependencies
FROM maven:3-jdk-11 AS maven
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve

# Classes
COPY src/main/java ./src/main/java
COPY src/main/resources ./src/main/resources
RUN mvn -e -B clean package -DskipTests -Dmaven.javadoc.skip=true

# Copy the jar and build image
FROM gcr.io/distroless/java:11
COPY --from=maven --chown=65532:65532 /app/target/dataspace-connector-*.jar /app/app.jar
WORKDIR /app
EXPOSE 8080
USER nonroot
ENTRYPOINT ["java","-jar","app.jar"]