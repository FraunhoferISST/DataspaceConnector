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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

/**
 * Service class to retrieve information about the project.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class ProjectInformationService {

    /**
     * Service for http connections.
     */
    private final @NonNull HttpService httpService;

    /**
     * The currently used project version.
     */
    @Value("${version}")
    private String projectVersion;

    /**
     * The repository owner.
     */
    private static final String REPOSITORY_OWNER = "International-Data-Spaces-Association";

    /**
     * The repository.
     */
    private static final String REPOSITORY = "DataspaceConnector";

    /**
     * The host address.
     */
    private static final String HOST = "api.github.com";

    /**
     * Limitation for the results.
     */
    private static final String RESULT_PER_PAGE = "1";

    /**
     * This method compares the release version with the currently used project version and decides
     * if an update is existing.
     *
     * @return Message if an update exists or not.
     * @throws IOException if an error occurs when retrieving the release version.
     */
    public ResponseEntity<Object> projectUpdateAvailable() throws IOException {
        ResponseEntity<Object> response;
        final var releaseVersion = getLatestReleaseVersion();

        final var releaseInfo = releaseVersion.split("\\.");
        final var projectInfo = projectVersion.split("\\.");

        if (Integer.parseInt(releaseInfo[0]) > Integer.parseInt(projectInfo[0])
                || Integer.parseInt(releaseInfo[1]) > Integer.parseInt(projectInfo[1])
                || Integer.parseInt(releaseInfo[2]) > Integer.parseInt(projectInfo[2])) {
            response = new ResponseEntity<>(releaseVersion, HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return response;
    }

    /**
     * Determines via the GitHub which is the latest release.
     *
     * @return The project version.
     * @throws IOException if an error occurs when retrieving the release version.
     */
    public String getLatestReleaseVersion() throws IOException {
        final var builder = getRequestBuilder();
        final var urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host(HOST)
                .addPathSegments("repos/" + REPOSITORY_OWNER + "/" + REPOSITORY + "/releases")
                .addQueryParameter("per_page", RESULT_PER_PAGE);
        final var url = urlBuilder.build();
        builder.url(url);
        builder.get();
        final var request = builder.build();

        final var response = httpService.send(request);
        final var responseBody = checkResponseNotNull(response);

        return identifyProjectVersion(responseBody);
    }

    private String identifyProjectVersion(final String response) {
        final var jsonArray = new JSONArray(response);
        return jsonArray.getJSONObject(0).get("tag_name").toString().substring(1);
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
}
