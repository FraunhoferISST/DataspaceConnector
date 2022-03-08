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

import io.dataspaceconnector.model.datasource.DataSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Entity for managing generic endpoints.
 */
@SQLDelete(sql = "UPDATE endpoint SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class GenericEndpoint extends Endpoint {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Data source to which the generic endpoint belongs.
     */
    @OneToOne
    private DataSource dataSource;

    /**
     * Holds the information about the endpoint type.
     */
    private String type;

    /**
     * Default constructor.
     */
    public GenericEndpoint() {
        super();
    }
}
