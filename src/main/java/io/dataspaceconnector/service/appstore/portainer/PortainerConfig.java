/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Portainer.
 */
@Data
@Configuration
public class PortainerConfig {

    /**
     * The scheme.
     */
    @Value("${portainer.application.scheme:http}")
    private String scheme;

    /**
     * The host.
     */
    @Value("${portainer.application.host}")
    private String host;

    /**
     * The port.
     */
    @Value("${portainer.application.port}")
    private Integer port;

    /**
     * The user for the authentication.
     */
    @Value("${portainer.application.username}")
    private String username;

    /**
     * The password for the authentication.
     */
    @Value("${portainer.application.password}")
    private String password;


}
