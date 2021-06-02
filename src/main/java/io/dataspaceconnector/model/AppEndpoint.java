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
package io.dataspaceconnector.model;

import io.dataspaceconnector.model.utils.UriConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.net.URI;

/**
 * The app endpoint can be used and connected to other endpoints to perform operations on the data.
 */
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class AppEndpoint extends Endpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the endpoint.
     */
    @Convert(converter = UriConverter.class)
    private URI accessURL;

    /**
     * The file name extension of the data.
     */
    private String mediaType;

    /**
     * The port number of the app endpoint.
     */
    private int appEndpointPort;

    /**
     * The protocol of the app endpoint.
     */
    private String appEndpointProtocol;

    /**
     * The used language.
     */
    private String language;

    /**
     * The type of the app endpoint.
     */
    @Enumerated(EnumType.STRING)
    private AppEndpointType appEndpointType;

    /**
     * Default constructor.
     */
    public AppEndpoint() {
        super();
    }
}
