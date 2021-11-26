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
package io.dataspaceconnector.extension.monitoring.update;

import de.fraunhofer.ids.messaging.protocol.http.HttpService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.dataspaceconnector.config.ConnectorConfig;
import io.dataspaceconnector.extension.monitoring.update.util.Repository;
import io.dataspaceconnector.extension.monitoring.update.util.UpdateType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.Request;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service class to retrieve information about the latest project release.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateInfoService {

    /**
     * Service for connector configurations.
     */
    private final @NonNull ConnectorConfig connectorConfig;

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpSvc;

    /**
     * Config for repository.
     */
    private final Repository repository = new Repository(
            443, "api.github.com", "https",
            "International-Data-Spaces-Association", "DataspaceConnector");

    /**
     * Compares latest release with the current version.
     *
     * @return Response map with current and update data.
     * @throws IOException If an error occurs when retrieving the release version.
     */
    public Map<String, Object> getUpdateDetails() throws IOException {
        final var version = connectorConfig.getDefaultVersion();

        final var updateInfo = getLatestInformation();

        if (isOutdated(version, updateInfo.get("version").toString())) {
            final var latestVersion = updateInfo.get("version").toString().split("\\.");
            final var currentVersion = version.split("\\.");
            final var type = getUpdateType(latestVersion, currentVersion);

            // Note: If the versions are not compatible, this is no guarantee that a newer version
            // is online. As the getUpdateType methods checks for greater numbers, we can assume
            // that unequal values + type NO_UPDATE implies that no newer version is available.
            if (type == UpdateType.NO_UPDATE) {
                updateInfo.clear();
                updateInfo.put("available", false);
            } else {
                // If update is available, set boolean to true and show more details.
                updateInfo.put("available", true);
                updateInfo.put("type", type.toString());
            }
        } else {
            // If no update is available, set boolean to false and show no more details.
            updateInfo.clear();
            updateInfo.put("available", false);
        }

        return updateInfo;
    }

    /**
     * Determines the latest release by a GitHub call.
     *
     * @return Data that should be displayed in case of an available update.
     * @throws IOException if an error occurs while retrieving the latest release information.
     */
    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    public Map<String, Object> getLatestInformation() throws IOException {
        final var url = new HttpUrl.Builder()
                .scheme(repository.getScheme())
                .host(repository.getHost())
                .port(repository.getPort())
                .addPathSegments("repos/" + repository.getOwner() + "/" + repository.getName()
                        + "/releases/latest")
                .build();

        final var response = httpSvc.send(new Request.Builder().url(url).get().build());

        try {
            return parseResponse(Objects.requireNonNull(response.body()).string());
        } catch (NullPointerException | IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not read response from GitHub. [exception=({})]", e.getMessage());
            }
            return new HashMap<>();
        }
    }

    /**
     * Parses the necessary information from the http response.
     *
     * @param response The response of the GitHub request.
     * @return Necessary data to determine whether there is an update and additional information.
     */
    private static Map<String, Object> parseResponse(final String response) {
        final var responseObj = new JSONObject(response);
        final var latestTag = responseObj.get("tag_name").toString().replace("v", "");

        final var release = new HashMap<String, Object>();
        release.put("location", responseObj.get("html_url").toString());
        release.put("version", latestTag.trim());

        return release;
    }

    /**
     * Checks the latest release version number against the current (SemVer).
     *
     * @param release Version number of the latest release.
     * @param project Current version number.
     * @return Result and type of the present update.
     */
    private static UpdateType getUpdateType(final String[] release, final String[] project) {
        try {
            if (Integer.parseInt(release[0]) > Integer.parseInt(project[0])) {
                return UpdateType.MAJOR;
            }

            if (Integer.parseInt(release[0]) == Integer.parseInt(project[0])
                    && Integer.parseInt(release[1]) > Integer.parseInt(project[1])) {
                return UpdateType.MINOR;
            }

            if (Integer.parseInt(release[0]) == Integer.parseInt(project[0])
                    && Integer.parseInt(release[1]) == Integer.parseInt(project[1])
                    && Integer.parseInt(release[2]) > Integer.parseInt(cleanVersion(project[2]))) {
                return UpdateType.PATCH;
            }
        } catch (IllegalArgumentException ignored) {
        }

        return UpdateType.NO_UPDATE;
    }

    private static boolean isOutdated(final String currentVersion, final String latestVersion) {
        return !currentVersion.equals(latestVersion);
    }

    private static String cleanVersion(final String string) {
        if (string.contains("SNAPSHOT")) {
            return string.replace("-SNAPSHOT", "");
        }

        return string;
    }
}
