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

        var networkID = portainerRequestSvc.createNetwork("testnet", true, false);
        //Exmaple AppStore Template describing an App (Docker-Image/Container)
        String appStoreTemplate = "{\n" +
                "    \"type\": 1,\n" +
                "    \"title\": \"Nginx\",\n" +
                "    \"description\": \"High performance web server\",\n" +
                "    \"categories\": [\n" +
                "        \"webserver\"\n" +
                "    ],\n" +
                "    \"platform\": \"linux\",\n" +
                "    \"logo\": \"https://portainer-io-assets.sfo2.digitaloceanspaces.com/logos/nginx.png\",\n" +
                "    \"image\": \"nginx:latest\",\n" +
                "    \"ports\": [\n" +
                "        \"80/tcp\",\n" +
                "        \"443/tcp\"\n" +
                "    ],\n" +
                "    \"volumes\": [\n" +
                "        {\n" +
                "            \"container\": \"/etc/nginx\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"container\": \"/usr/share/nginx/html\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"registry\":\"registry.hub.docker.com/library\",\n" +
                "}";

        //1. Create Registry with given information from AppStore template
        //TODO: Where does the AppStore template provide credentials for private registry?
        portainerRequestSvc.createRegistry(appStoreTemplate);

        //2. Pull Image with given information from AppStore template
        portainerRequestSvc.pullImage(appStoreTemplate);


        //3. Create volumes with given information from AppStore template
        final var volumeMap = portainerRequestSvc.createVolumes(appStoreTemplate);

        //4. Create Container with given information from AppStore template and new volume
        final var containerId = portainerRequestSvc.createContainer(appStoreTemplate, volumeMap);

        //POST: http://localhost:9000/api/endpoints/1/docker/networks/{networkID}/connect
        //payload: {"Container":"{containerID}"}
        var response = portainerRequestSvc.joinNetwork(containerId, networkID);
        var string = response.body().string();

        //5. Start the App (container)
        portainerRequestSvc.startContainer(containerId);


    }
}
