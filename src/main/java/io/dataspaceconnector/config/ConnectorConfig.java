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
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.util.List;

/**
 * This class handles policy settings: negotiation, pattern support, and usage control framework.
 */
@Getter
@Setter
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
    private static final String OUTBOUND_VERSION = "4.2.6";

    /**
     * The inbound model versions.
     */
    private static final List<String> INBOUND_VERSIONS = List.of("4.0.0", "4.0.2", "4.0.3", "4.0.4",
            "4.0.5", "4.0.6", "4.0.7", "4.0.8", "4.0.9", "4.0.10", "4.0.11", "4.1.0", "4.1.1",
            "4.1.2", "4.1.3", "4.2.0", "4.2.1", "4.2.2", "4.2.3", "4.2.4", "4.2.5", "4.2.6");

    /**
     * Get the outbound model version.
     *
     * @return The model version.
     */
    public String getOutboundVersion() {
        return OUTBOUND_VERSION;
    }

    /**
     * Get the inbound model versions.
     *
     * @return The model versions.
     */
    public List<String> getInboundVersions() {
        return INBOUND_VERSIONS;
    }
}
