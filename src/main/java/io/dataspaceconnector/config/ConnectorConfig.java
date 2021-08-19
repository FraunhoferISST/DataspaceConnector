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
package io.dataspaceconnector.config;

import io.dataspaceconnector.common.ids.policy.UsageControlFramework;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

/**
 * This class handles policy settings: negotiation, pattern support, and usage control framework.
 */
@Data
@Configuration
public class ConnectorConfig {
    /**
     * The clearing house access url.
     */
    @Value("${clearing.house.url:}")
    private URI clearingHouse;

    /**
     * The policy negotiation status from application.properties.
     */
    @Value("${policy.negotiation}")
    private boolean policyNegotiation;

    /**
     * Setting for allowing unsupported patterns from application.properties.
     */
    @Value("${policy.allow-unsupported-patterns}")
    private boolean allowUnsupported;

    /**
     * Usage control framework from application.properties.
     */
    @Value("${policy.framework}")
    private UsageControlFramework ucFramework;

    /**
     * Indicates whether IDSCP protocol is enabled or not.
     */
    @Value("${idscp2.enabled}")
    private boolean idscpEnabled;

    /**
     * The default version.
     */
    @Value("${version}")
    private String defaultVersion;

    /**
     * The outbound model version.
     */
    private final String outboundVersion = "4.1.2";

    /**
     * The inbound model versions.
     */
    private final List<String> inboundVersions = List.of("4.0.0", "4.1.0", "4.1.2");
}
