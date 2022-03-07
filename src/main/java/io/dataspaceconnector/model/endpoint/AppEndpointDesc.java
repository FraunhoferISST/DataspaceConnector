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
package io.dataspaceconnector.model.endpoint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Describes an app endpoint. Use this structure to create
 * or update an app endpoint.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AppEndpointDesc extends EndpointDesc {

    /**
     * Holds the information about the endpoint type.
     */
    private String endpointType;

    /**
     * Port of the Endpoint.
     */
    private int endpointPort;

    /**
     * Endpoint accepted mediatype.
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
