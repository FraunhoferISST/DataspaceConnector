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
package io.dataspaceconnector.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dataspaceconnector.model.keystore.KeystoreDesc;
import io.dataspaceconnector.model.named.NamedDescription;
import io.dataspaceconnector.model.proxy.ProxyDesc;
import io.dataspaceconnector.model.truststore.TruststoreDesc;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.util.List;

/**
 * Describing the configuration's properties.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConfigurationDesc extends NamedDescription {

    /**
     * The id of the connector.
     */
    private URI connectorId;

    /**
     * The access url of the connector.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI defaultEndpoint;

    /**
     * The project version.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String version;

    /**
     * The curator.
     */
    private URI curator;

    /**
     * The maintainer.
     */
    private URI maintainer;

    /**
     * The list of inbound model version.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<String> inboundModelVersion;

    /**
     * The outbound model version.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String outboundModelVersion;

    /**
     * The security profile.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private SecurityProfile securityProfile;

    /**
     * The log level.
     */
    private LogLevel logLevel;

    /**
     * The status of the connector.
     */
    private ConnectorStatus status;

    /**
     * The deploy mode of the connector.
     */
    private DeployMode deployMode;

    /**
     * The truststore settings.
     */
    private TruststoreDesc truststore;

    /**
     * The proxy settings.
     */
    private ProxyDesc proxy;

    /**
     * The keystore settings.
     */
    private KeystoreDesc keystore;
}
