# Build the jar
FROM maven:latest AS maven

COPY pom.xml /tmp/

WORKDIR tmp
RUN mvn verify clean --fail-never

COPY src /tmp/src/

RUN mvn clean package -DskipTests -Dmaven.javadoc.skip=true

# Copy the jar and build image
FROM gcr.io/distroless/java:11
COPY --from=maven --chown=65532:65532 /tmp/target/*.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

USER nonroot

ENTRYPOINT ["java","-jar","app.jar"]