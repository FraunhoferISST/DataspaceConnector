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

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import de.fraunhofer.iais.eis.ConnectorStatus;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.keystore.Keystore;
import io.dataspaceconnector.model.proxy.Proxy;
import io.dataspaceconnector.model.truststore.Truststore;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

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
public class Configuration extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

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
    @OneToOne(optional = true)
    private Proxy proxy;

    /**
     * The trust store.
     */
    @OneToOne(optional = true)
    private Truststore truststore;

    /**
     * The key store.
     */
    @OneToOne(optional = true)
    private Keystore keystore;
}
