/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.service.appstore.container;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Request body creator for the container api.
 */
@Component
public class ContainerRequestBodyCreator {

    /**
     * @param username The username for the authentication.
     * @param password The password for the authentication.
     * @return request body as string.
     */
    public final String createRequestBodyForAuthentication(final String username,
                                                           final String password) {
        final var jsonObject = new JSONObject();
        jsonObject.put("Username", username);
        jsonObject.put("Password", password);

        return jsonObject.toString();
    }

    /**
     * @param containerConfiguration The container configuration.
     * @return request body as string.
     */
    public final String createContainerConfigRequestBody(
            final ContainerConfiguration containerConfiguration) {
        final var main = new JSONObject();

        if (containerConfiguration != null) {
            main.put("Image", containerConfiguration.getImageName());

            final var exposedPorts = new JSONObject();
            final var exposedPort = String.valueOf(containerConfiguration.getExposedPort());
            final var protocol = containerConfiguration.getProtocol();
            exposedPorts.put(exposedPort + "/" + protocol, new JSONObject());
            main.put("ExposedPorts", exposedPorts);

            final var hostConfig = new JSONObject();
            final var portBinding = new JSONObject();
            final var hostConfigs = new JSONArray();
            final var hostPort = new JSONObject();
            hostPort.put("HostPort", containerConfiguration.getHostPort());
            hostConfigs.put(hostPort);
            portBinding.put(containerConfiguration.getHostPort() + "/" + protocol, hostConfigs);
            hostConfig.put("PortBindings", portBinding);
            main.put("HostConfig", hostConfig);
        }
        return main.toString();
    }
}
