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
package io.dataspaceconnector.service;

import de.fraunhofer.ids.messaging.protocol.http.HttpService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
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
public class ProjectInformationService {

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpSvc;

    /**
     * The currently used project version.
     */
    @Value("${version}")
    private String projectVersion;

    /**
     * The scheme.
     */
    private static final String SCHEME = "https";

    /**
     * GitHub API host address.
     */
    private static final String HOST = "api.github.com";

    /**
     * The port.
     */
    private static final Integer PORT = 443;

    /**
     * The project repository owner.
     */
    private static final String REPOSITORY_OWNER = "International-Data-Spaces-Association";

    /**
     * The project repository.
     */
    private static final String REPOSITORY = "DataspaceConnector";

    /**
     * config for repository.
     */
    private RepoConfig repoConfig = new RepoConfig(PORT, HOST, SCHEME);

    /**
     * Compares latest release with the current version.
     *
     * @return Response-Map with current and update data.
     * @throws IOException If an error occurs when retrieving the release version.
     */
    public Map<String, Object> projectUpdateAvailable() throws IOException {
        final var versionInfo = new HashMap<String, Object>();
        final var latestData = getLatestData();
        final var latestVersion = latestData.get("update.version").split("\\.");
        final var currentVersion = projectVersion.split("\\.");
        final var updateType = isOutdated(latestVersion, currentVersion);

        latestData.put("update.type", updateType.toString());
        latestData.put("update.available", Udpate.NO_UPDATE.equals(updateType) ? "false" : "true");

        versionInfo.put("connector.update", latestData);
        versionInfo.put("connector.version", projectVersion);

        return versionInfo;
    }

    /**
     * Determines by a GitHub API call the latest release.
     *
     * @return Data of the API, which should be displayed in case of an available update.
     * @throws IOException If an error occurs while retrieving the latest release information.
     */
    public Map<String, String> getLatestData() throws IOException {
        final var builder = new Request.Builder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme(repoConfig.getScheme())
                .host(repoConfig.getHost())
                .port(repoConfig.getPort())
                .addPathSegments("repos/" + REPOSITORY_OWNER + "/" + REPOSITORY
                        + "/releases/latest");

        final var url = urlBuilder.build();
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpSvc.send(request);
        final var responseBody = checkResponseNotNull(response);

        return parseResponse(responseBody);
    }

    /**
     * Takes the necessary information from the API response.
     *
     * @param response The answer of the GitHub API request.
     * @return The necessary data to determine whether there is an update and
     * additional display data.
     */
    private Map<String, String> parseResponse(final String response) {
        final var responseObj = new JSONObject(response);
        final var latestTag = responseObj.get("tag_name").toString().replace("v", "");

        final var release = new HashMap<String, String>();
        release.put("update.location", responseObj.get("html_url").toString());
        release.put("update.version", latestTag.trim());

        return release;
    }

    /**
     * Ensures that the API response is valid.
     *
     * @param response The API response.
     * @return The response body of the API request.
     * @throws IOException If there is a non-valid API response.
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private String checkResponseNotNull(@NonNull final Response response) throws IOException {
        final var checkedResp = Objects.requireNonNull(response);
        final var body = Objects.requireNonNull(checkedResp.body());

        return Objects.requireNonNull(body.string());
    }

    /**
     * Checks the latest release version number against the current (SemVer).
     *
     * @param releaseInfo Version number of the latest release.
     * @param projectInfo Current version number.
     * @return Result and type of the present update.
     */
    private Udpate isOutdated(final String[] releaseInfo, final String[] projectInfo) {
        if (Integer.parseInt(releaseInfo[0]) > Integer.parseInt(projectInfo[0])) {
            return Udpate.MAJOR;
        }

        if (Integer.parseInt(releaseInfo[0]) == Integer.parseInt(projectInfo[0])
                && Integer.parseInt(releaseInfo[1]) > Integer.parseInt(projectInfo[1])) {
            return Udpate.MINOR;
        }

        if (Integer.parseInt(releaseInfo[0]) == Integer.parseInt(projectInfo[0])
                && Integer.parseInt(releaseInfo[1]) == Integer.parseInt(projectInfo[1])
                && Integer.parseInt(releaseInfo[2]) > Integer.parseInt(projectInfo[2])) {
            return Udpate.PATCH;
        }

        return Udpate.NO_UPDATE;
    }

    /**
     * Configuration for accessed repository.
     */
    @Getter
    @AllArgsConstructor
    public static class RepoConfig {

        /**
         * The port.
         */
        private int port;

        /**
         * The hostname.
         */
        private String host;

        /**
         * The scheme.
         */
        private String scheme;
    }

    /**
     * Types which updates may be present.
     */
    private enum Udpate {

        /**
         * If no update is available.
         */
        NO_UPDATE("no update"),

        /**
         * A new major release is available.
         */
        MAJOR("major"),

        /**
         * A new minor release is available.
         */
        MINOR("minor"),

        /**
         * A new patch release is available.
         */
        PATCH("patch");

        /**
         * Holds the enums string.
         */
        private final String value;

        /**
         * Constructor.
         *
         * @param name The name of the update-enum .
         */
        Udpate(final String name) {
            this.value = name;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
