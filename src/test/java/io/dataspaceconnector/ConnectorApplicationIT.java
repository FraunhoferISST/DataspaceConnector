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
        //DSC UI Flow (current AppStore endpoint for IDS-Messages: https://binac.fit.fraunhofer.de/api/ids/data):
        // 1. POST /api/appstores - create new AppStore
        //    - AppStore can be requested for App catalog
        // 2. POST /api/ids/app with Recipient URI - DescriptionRequestMessage to AppStore (returns App catalog?)
        //    - DSC UI shows App catalog of AppStore (?)
        // 3. POST /api/ids/app with Recipient and App URI - DescriptionRequestMessage to AppStore to get metadata of single App and App-Template
        //    - The following flow begins automatically with 3. and deploys the App involving the AppStore Registry

        //Exmaple AppStore Template describing an App (Docker-Image/Container)
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

        //1. Create Registry with given information from AppStore template
        //TODO: Where does the AppStore template provide credentials for private registry?
        portainerRequestSvc.createRegistry(appStoreTemplate);

        //2. Pull Image with given information from AppStore template
        portainerRequestSvc.pullImage(appStoreTemplate);

        //3. Create volumes with given information from AppStore template
        final var volumeMap = portainerRequestSvc.createVolumes(appStoreTemplate);

        //4. Create Container with given information from AppStore template and new volume
        //TODO: Check if same network and stack necessary?
        final var containerId = portainerRequestSvc.createContainer(appStoreTemplate, volumeMap);

        //5. Start the App (container)
        portainerRequestSvc.startContainer(containerId);
    }
}
