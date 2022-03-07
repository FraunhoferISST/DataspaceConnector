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
package io.dataspaceconnector.controller.resource.view.endpoint;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.view.util.ViewConstants;
import io.dataspaceconnector.model.endpoint.EndpointType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;

/**
 * A DTO for controlled exposing of app endpoint information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = BaseType.ENDPOINTS, itemRelation = "endpoint")
public class AppEndpointView extends RepresentationModel<AppEndpointView> {

    /**
     * The endpoint type.
     */
    private final EndpointType type = EndpointType.APP;

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime modificationDate;

    /**
     * The location information.
     */
    private String location;

    /**
     * Holds the information about the endpoint type.
     */
    private String endpointType;

    /**
     * The documentation of the endpoint.
     */
    private URI docs;

    /**
     * The information of the endpoint.
     */
    private String info;

    /**
     * Port of the Endpoint.
     */
    private int endpointPort;

    /**
     * Endpoint accepted media type.
     */
    private String mediaType;

    /**
     * Protocol used by endpoint.
     */
    private String protocol;

    /**
     * Language of the endpoint.
     */
    private String language;

    /**
     * Endpoint path suffix.
     */
    private String path;
}
