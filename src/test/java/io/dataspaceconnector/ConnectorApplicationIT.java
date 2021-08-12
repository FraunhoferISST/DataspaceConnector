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
				"    \"registry\":\"https://registry.hub.docker.com/library\",\n" +
				"}";
		portainerRequestSvc.createRegistry(appStoreTemplate);
		portainerRequestSvc.pullImage(appStoreTemplate);
		var volumeMap = portainerRequestSvc.createVolumes(appStoreTemplate);
		var containerId = portainerRequestSvc.createContainer(appStoreTemplate, volumeMap);
		portainerRequestSvc.startContainer(containerId);
	}
}
