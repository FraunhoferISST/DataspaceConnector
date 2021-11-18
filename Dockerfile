#
# Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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

# Build environment
FROM maven:3-jdk-11 AS maven
WORKDIR /app
## Dependencies
COPY pom.xml .
RUN mvn -e -B dependency:resolve
RUN mvn -e -B dependency:resolve-plugins

## Classes
COPY src/main/java ./src/main/java
COPY src/main/resources ./src/main/resources
RUN mvn -e -B clean package -DskipTests -Dmaven.javadoc.skip=true
RUN java -Djarmode=layertools -jar ./target/dsc.jar extract


# Final image
## Copy the jar and build image
FROM gcr.io/distroless/java-debian11:11
COPY --from=maven /app/spring-boot-loader/ /app/
COPY --from=maven /app/dependencies/ /app/
## COPY --from=maven /app/snapshot-dependencies/ /app/ # DO NOT USE SNAPSHOTS
COPY --from=maven /app/application/ /app/
WORKDIR /app
EXPOSE 8080
EXPOSE 29292
USER nonroot
ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
