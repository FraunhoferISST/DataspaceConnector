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

import io.dataspaceconnector.common.net.HttpService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ProjectInformationServiceTest {

    private static String RESPONSE_STRING = "[{\"url\":\"https://api.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/releases/48802118\",\"assets_url\":\"https://api.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/releases/48802118/assets\",\"upload_url\":\"https://uploads.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/releases/48802118/assets{?name,label}\",\"html_url\":\"https://github.com/International-Data-Spaces-Association/DataspaceConnector/releases/tag/v6.2.0\",\"id\":48802118,\"author\":{\"login\":\"juliapampus\",\"id\":72392527,\"node_id\":\"MDQ6VXNlcjcyMzkyNTI3\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/72392527?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/juliapampus\",\"html_url\":\"https://github.com/juliapampus\",\"followers_url\":\"https://api.github.com/users/juliapampus/followers\",\"following_url\":\"https://api.github.com/users/juliapampus/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/juliapampus/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/juliapampus/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/juliapampus/subscriptions\",\"organizations_url\":\"https://api.github.com/users/juliapampus/orgs\",\"repos_url\":\"https://api.github.com/users/juliapampus/repos\",\"events_url\":\"https://api.github.com/users/juliapampus/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/juliapampus/received_events\",\"type\":\"User\",\"site_admin\":false},\"node_id\":\"MDc6UmVsZWFzZTQ4ODAyMTE4\",\"tag_name\":\"v6.2.0\",\"target_commitish\":\"main\",\"name\":\"Dataspace Connector v6.2.0 - AppStore communication\",\"draft\":false,\"prerelease\":false,\"created_at\":\"2021-09-01T09:21:50Z\",\"published_at\":\"2021-09-01T09:23:51Z\",\"assets\":[],\"tarball_url\":\"https://api.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/tarball/v6.2.0\",\"zipball_url\":\"https://api.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/zipball/v6.2.0\",\"body\":\"The according documentation can be found [here](https://international-data-spaces-association.github.io/DataspaceConnector/CommunicationGuide/v6/IdsEcosystem/AppStore). In `CHANGELOG.md`:\\r\\n\\r\\n### Added\\r\\n- Add app, app store, and app endpoint entities to the data model.\\r\\n  - Provide REST endpoints for managing entities and its relations.\\r\\n  - Add REST endpoint for managing image/container deployment with Portainer.\\r\\n- Add `POST api/ids/app` endpoint for downloading an IDS app's metadata and data from the IDS AppStore.\",\"reactions\":{\"url\":\"https://api.github.com/repos/International-Data-Spaces-Association/DataspaceConnector/releases/48802118/reactions\",\"total_count\":2,\"+1\":0,\"-1\":0,\"laugh\":0,\"hooray\":1,\"confused\":0,\"heart\":1,\"rocket\":0,\"eyes\":0}}]";

    @Autowired
    private ProjectInformationService projectInformationService;

    @MockBean
    public HttpService httpService;

    @MockBean
    public ProjectInformationService.RepoConfig repoConfig;

    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws Exception {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Mockito.when(repoConfig.getHost()).thenReturn(mockWebServer.getHostName());
        Mockito.when(repoConfig.getPort()).thenReturn(mockWebServer.getPort());
        Mockito.when(repoConfig.getScheme()).thenReturn("http");

        var configField = projectInformationService.getClass().getDeclaredField("repoConfig");
        configField.setAccessible(true);
        configField.set(projectInformationService, repoConfig);
    }

    @Test
    public void testGetLatestReleaseVersion() throws Exception {
        /* ARRANGE */
        var mockResponse = new MockResponse()
                .setResponseCode(200)
                .setBody(RESPONSE_STRING);
        final var version = "6.2.0";

        mockWebServer.enqueue(mockResponse);

        Assertions.assertEquals(version,
                projectInformationService.getLatestReleaseInformation().get("updateVersion"));
    }
}
