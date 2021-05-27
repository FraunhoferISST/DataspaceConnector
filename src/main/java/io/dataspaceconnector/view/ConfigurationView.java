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
package io.dataspaceconnector.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.model.ConnectorDeployMode;
import io.dataspaceconnector.model.LogLevel;
import io.dataspaceconnector.model.Proxy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A DTO for controlled exposing of configuration information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "configurations", itemRelation = "configuration")
public class ConfigurationView extends RepresentationModel<ConfigurationView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime modificationDate;

    /**
     * The log level.
     */
    private LogLevel logLevel;

    /**
     * The deploy mode of the connector.
     */
    private ConnectorDeployMode deployMode;

    /**
     * The proxy configuration.
     */
    private List<Proxy> proxy;

    /**
     * The trust store.
     */
    private String trustStore;

    /**
     * The password of the trust store.
     */
    private String trustStorePassword;

    /**
     * The key store.
     */
    private String keyStore;

    /**
     * The key store password.
     */
    private String keyStorePassword;
}
