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
package io.dataspaceconnector;

import io.dataspaceconnector.service.appstore.portainer.PortainerRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ConnectorApplicationIT {

    @Autowired
    private PortainerRequestService portainerRequestSvc;

    @Test
    void contextLoads() throws Exception {
        final var appStoreTemplate = "{\n" +
                "    \"type\": 1,\n" +
                "    \"title\": \"Dataspace Connector\",\n" +
                "    \"description\": \"High performance IDS-Connector\",\n" +
                "    \"categories\": [\n" +
                "        \"ids\"\n" +
                "    ],\n" +
                "    \"platform\": \"linux\",\n" +
                "    \"logo\": \"https://camo.githubusercontent.com/23b18b3572aadf5a1e1a072165cfcb8537e18ec2c66b5cc6cc62b7170261960e/68747470733a2f2f6461746173706163652d636f6e6e6563746f722e64652f6473635f6c6f676f2e737667\",\n" +
                "    \"image\": \"dataspace-connector:latest\",\n" +
                "    \"ports\": [\n" +
                "        \"8080/tcp\",\n" +
                "    ],\n" +
                "    \"registry\":\"ghcr.io/international-data-spaces-association\",\n" +
                "}";
        portainerRequestSvc.createRegistry(appStoreTemplate);
        portainerRequestSvc.pullImage(appStoreTemplate);
        var volumeMap = portainerRequestSvc.createVolumes(appStoreTemplate);
        var containerId = portainerRequestSvc.createContainer(appStoreTemplate, volumeMap);
        portainerRequestSvc.startContainer(containerId);
    }
}

//java.io.IOException: Unexpected code Response{protocol=http/1.1, code=500, message=Internal Server Error,
// url=http://localhost:9000/api/endpoints/1/docker/images/create}
// With Body: {"message":"Get http:: http: no Host in request URL"}
