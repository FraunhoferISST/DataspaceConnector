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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
     * Limitation for the results.
     * E.g. 1 Provides only the latest release.
     */
    private static final Integer RESULT_PER_PAGE = 1;

    /**
     * config for repository.
     */
    private RepoConfig repoConfig = new RepoConfig(PORT, HOST, SCHEME);

    /**
     * This method compares the release version with the currently used project version and decides
     * if an update is existing.
     *
     * @return Response-Message if an update exists or not.
     * @throws IOException If an error occurs when retrieving the release version.
     */
    public ResponseEntity<Object> projectUpdateAvailable() throws IOException {
        ResponseEntity<Object> response;
        final var releaseInformation = getLatestReleaseInformation();

        final var releaseInfo = releaseInformation.get("updateVersion").toString().split("\\.");
        final var projectInfo = projectVersion.split("\\.");

        if (isUpdatable(releaseInfo, projectInfo)) {
            response = new ResponseEntity<>(releaseInformation.toString(), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return response;
    }

    /**
     * Checks the latest release version number against the used (SemVer).
     *
     * @param releaseInfo Version number of the latest release.
     * @param projectInfo Current version number.
     * @return True if a newer version is available, else false.
     */
    private boolean isUpdatable(final String[] releaseInfo, final String[] projectInfo) {
        final var newMajor = Integer.parseInt(releaseInfo[0]) > Integer.parseInt(projectInfo[0]);

        final var newMinor = (Integer.parseInt(releaseInfo[0]) == Integer.parseInt(projectInfo[0]))
                 && (Integer.parseInt(releaseInfo[1]) > Integer.parseInt(projectInfo[1]));

        final var newPatch = (Integer.parseInt(releaseInfo[0]) == Integer.parseInt(projectInfo[0]))
                && (Integer.parseInt(releaseInfo[1]) == Integer.parseInt(projectInfo[1]))
                && Integer.parseInt(releaseInfo[2]) > Integer.parseInt(projectInfo[2]);

        return newMajor || newMinor || newPatch;
    }

    /**
     * Determines via the GitHub which is the latest release.
     *
     * @return The project version.
     * @throws IOException if an error occurs when retrieving the release version.
     */
    public JSONObject getLatestReleaseInformation() throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme(repoConfig.getScheme())
                .host(repoConfig.getHost())
                .port(repoConfig.getPort())
                .addPathSegments("repos/" + REPOSITORY_OWNER + "/" + REPOSITORY + "/releases")
                .addQueryParameter("per_page", RESULT_PER_PAGE.toString());

        final var url = urlBuilder.build();
        builder.url(url);
        builder.get();

        final var request = builder.build();
        final var response = httpSvc.send(request);
        final var responseBody = checkResponseNotNull(response);

        return extractReleaseInformation(responseBody);
    }

    private JSONObject extractReleaseInformation(final String response) {
        final var releaseInformation = new JSONObject();
        final var jsonArray = new JSONArray(response);
        final var jsonObject = jsonArray.getJSONObject(0);

        var latestTag = jsonObject.get("tag_name").toString();

        if (latestTag.contains("v")) {
            latestTag = latestTag.replace("v", "");
        }

        releaseInformation.put("currentVersion", projectVersion);
        releaseInformation.put("updateVersion", latestTag.trim());
        releaseInformation.put("releaseUrl", jsonObject.get("html_url").toString());
        releaseInformation.put("prerelease", jsonObject.get("prerelease"));
        releaseInformation.put("draft", jsonObject.get("draft"));

        return releaseInformation;
    }

    private Request.Builder getRequestBuilder() {
        return new Request.Builder();
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    private String checkResponseNotNull(@NonNull final Response response) throws IOException {
        final var checkedResp = Objects.requireNonNull(response);
        final var body = Objects.requireNonNull(checkedResp.body());

        return Objects.requireNonNull(body.string());
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
}
