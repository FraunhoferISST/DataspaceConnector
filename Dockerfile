#
# Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# Build application
FROM maven:3-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
## Dependencies
RUN mvn -e -B dependency:resolve && \
    mvn -e -B dependency:resolve-plugins
## Classes
COPY src/main/java ./src/main/java
COPY src/main/resources ./src/main/resources
## Build
RUN mvn -e -B clean package -DskipTests -Dmaven.javadoc.skip=true && \
    java -Djarmode=layertools -jar /app/target/dataspaceconnector.jar extract

# JRE
FROM eclipse-temurin:17 as jre-builder
RUN jlink \
    --add-modules java.base,java.compiler,java.datatransfer,java.desktop,java.instrument,java.logging,java.management,java.management.rmi,java.naming,java.net.http,java.prefs,java.rmi,java.scripting,java.se,java.security.jgss,java.security.sasl,java.smartcardio,java.sql,java.sql.rowset,java.transaction.xa,java.xml,java.xml.crypto,jdk.accessibility,jdk.charsets,jdk.crypto.cryptoki,jdk.crypto.ec,jdk.dynalink,jdk.httpserver,jdk.internal.vm.ci,jdk.internal.vm.compiler,jdk.internal.vm.compiler.management,jdk.jdwp.agent,jdk.jfr,jdk.jsobject,jdk.localedata,jdk.management,jdk.management.agent,jdk.management.jfr,jdk.naming.dns,jdk.naming.rmi,jdk.net,jdk.nio.mapmode,jdk.sctp,jdk.security.auth,jdk.security.jgss,jdk.unsupported,jdk.xml.dom,jdk.zipfs \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /jre

# Base image
# hadolint ignore=DL3007
FROM gcr.io/distroless/java-base:latest as base
ENV JAVA_HOME=/opt/java/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-builder /jre $JAVA_HOME

# Final image
FROM base
WORKDIR /app
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/application/ ./
EXPOSE 8080
EXPOSE 29292
USER nonroot
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]
