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
package io.dataspaceconnector.service.appstore.portainer;

import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.SocketUtils;

import java.util.List;
import java.util.Map;

/**
 * Helper class to outsource the creation of json payload.
 */
public final class PortainerUtil {

    private PortainerUtil() {
        // not used
    }

    /**
     * @param templateObject The template object.
     * @param ports          The list of ports.
     * @param volumes        Map of volumes.
     * @param image          The image name.
     * @return payload for the creation of a container.
     */
    public static JSONObject createContainerJSONPayload(final JSONObject templateObject,
                                                        final List<String> ports,
                                                        final Map<String, String> volumes,
                                                        final String image) {

        // Build json payload and fill single fields.
        final var jsonPayload = new JSONObject() {{
            put("Env", new JSONArray());
            put("OpenStdin", false);
            put("Tty", false);
            put("Labels", new JSONObject());
            put("name", "");
            put("Cmd", new JSONArray());
            put("Image", templateObject.getString("registry")
                    + "/" + templateObject.getString("image"));
        }};

        // Build exposed ports part of json payload.
        final var exposedPorts = new JSONObject();
        for (final var port : ports) {
            exposedPorts.put(port, new JSONObject());
        }
        jsonPayload.put("ExposedPorts", exposedPorts);

        // Build hostConfig part of json payload.
        final var hostConfig = new JSONObject() {{
            put("Privileged", false);
            put("ExtraHosts", new JSONArray());
            put("NetworkMode", "bridge");
        }};

        final String restartPolicy;
        if (templateObject.has("restart_policy")) {
            restartPolicy = templateObject.getString("restart_policy");
        } else {
            restartPolicy = "always";
        }
        hostConfig.put("RestartPolicy",
                new JSONObject(String.format("{\"Name\":\"%s\"}", restartPolicy)));

        final var portBindings = new JSONObject();
        for (final var port : ports) {
            portBindings.put(port, new JSONArray().appendElement(new JSONObject()
                    .put("HostPort", String.valueOf(SocketUtils.findAvailableTcpPort()))));
        }
        hostConfig.put("PortBindings", portBindings);

        final var binds = new JSONArray();
        for (var bind : volumes.entrySet()) {
            binds.appendElement(bind.getValue() + ":" + bind.getKey());
        }
        hostConfig.put("Binds", binds);

        jsonPayload.put("HostConfig", hostConfig);
        jsonPayload.put("name", image);

        // Build volumes part of json payload.
        final var volumesJSON = new JSONObject();
        for (var bind : volumes.entrySet()) {
            volumesJSON.put(bind.getKey(), new JSONObject());
        }
        jsonPayload.put("Volumes", volumesJSON);

        return jsonPayload;
    }

    /**
     * @param networkName name of network to create
     * @param pub         true, if network should be public
     * @param adminOnly   true if only visible by admin
     * @return json payload for network creation requests
     */
    public static JSONObject createNetworkPayload(final String networkName,
                                                  final boolean pub,
                                                  final boolean adminOnly) {
        final var resourceControl = new JSONObject() {{
            put("TeamAccess", new org.json.JSONArray());
            put("Public", pub);
            put("AdministratorOnly", adminOnly);
            put("System", false);
        }};

        final var portainerJson = new JSONObject() {{
            put("ResourceControl", resourceControl);
        }};

        return new JSONObject() {{
            put("Name", networkName);
            put("CheckDuplicate", true);
            put("Portainer", portainerJson);
            put("Warning", "");
        }};
    }
}
