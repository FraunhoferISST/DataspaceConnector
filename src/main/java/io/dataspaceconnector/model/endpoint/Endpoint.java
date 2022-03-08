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

import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.util.UriConverter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.net.URI;

import static io.dataspaceconnector.model.config.DatabaseConstants.ENDPOINT_LOCATION_LENGTH;
import static io.dataspaceconnector.model.config.DatabaseConstants.URI_COLUMN_LENGTH;

/**
 * Entity which manages the endpoints.
 */
@javax.persistence.Entity
@Inheritance
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "endpoint")
@RequiredArgsConstructor
public class Endpoint extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**+
     * The access url of the endpoint.
     */
    @Column(length = ENDPOINT_LOCATION_LENGTH)
    private String location;

    /**
     * The documentation for the endpoint.
     */
    @Convert(converter = UriConverter.class)
    @Column(length = URI_COLUMN_LENGTH)
    private URI docs;

    /**
     * The information about the endpoint.
     */
    private String info;

    /**
     * The type information.
     */
    private String type;
}
