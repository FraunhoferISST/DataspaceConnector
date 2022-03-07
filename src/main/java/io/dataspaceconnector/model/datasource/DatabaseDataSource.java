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

import javax.persistence.Entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Subclass of {@link DataSource} that holds information specific for databases.
 */
@SQLDelete(sql = "UPDATE datasource SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Entity
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
public class DatabaseDataSource extends DataSource {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Creates a DatabaseDataSource with default type DATABASE.
     */
    public DatabaseDataSource() {
        this.setType(DataSourceType.DATABASE);
    }

    /**
     * JDBC URL of the database.
     */
    private String url;

    /**
     * Name of the driver class to use for connecting to the database.
     */
    private String driverClassName;

}
