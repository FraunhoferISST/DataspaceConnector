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
package io.dataspaceconnector.model.datasource;

import io.dataspaceconnector.model.auth.Authentication;
import io.dataspaceconnector.model.base.Entity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Inheritance;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Entity which holds information about the data sources.
 */
@javax.persistence.Entity
@Inheritance
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql = "UPDATE datasource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Table(name = "datasource")
public class DataSource extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Creates a DataSource with default type REST.
     */
    public DataSource() {
        this.type = DataSourceType.REST;
    }

    /**
     * The authentication for the data source.
     */
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Authentication authentication;

    /**
     * The type of the data source.
     */
    @Transient
    private DataSourceType type;
}
