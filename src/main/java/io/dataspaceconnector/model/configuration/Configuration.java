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

import io.dataspaceconnector.model.keystore.Keystore;
import io.dataspaceconnector.model.named.NamedEntity;
import io.dataspaceconnector.model.proxy.Proxy;
import io.dataspaceconnector.model.truststore.Truststore;
import io.dataspaceconnector.model.util.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * The configuration describes the configuration of a connector.
 */
@javax.persistence.Entity
@Table(name = "configuration")
@SQLDelete(sql = "UPDATE configuration SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Configuration extends NamedEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The id of the connector.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI connectorId;

    /**
     * The access url of the connector.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI defaultEndpoint;

    /**
     * The project version.
     */
    private String version;

    /**
     * The curator.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI curator;

    /**
     * The maintainer.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI maintainer;

    /**
     * The list of inbound model version.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> inboundModelVersion;

    /**
     * The outbound model version.
     */
    private String outboundModelVersion;

    /**
     * The security profile.
     */
    @Enumerated(EnumType.STRING)
    private SecurityProfile securityProfile;

    /**
     * The log level.
     */
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;

    /**
     * The status of the connector.
     */
    @Enumerated(EnumType.STRING)
    private ConnectorStatus status;

    /**
     * The deploy mode of the connector.
     */
    @Enumerated(EnumType.STRING)
    private DeployMode deployMode;

    /**
     * The proxy configuration.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Proxy proxy;

    /**
     * The trust store.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Truststore truststore;

    /**
     * The key store.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Keystore keystore;

    /**
     * Weather this config is the active one.
     */
    @Column(unique = true)
    private Boolean active;
}
