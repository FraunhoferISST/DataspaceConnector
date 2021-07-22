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
package io.dataspaceconnector.model.app;

import io.dataspaceconnector.model.endpoint.AppEndpoint;
import io.dataspaceconnector.model.named.NamedEntity;
import io.dataspaceconnector.service.usagecontrol.PolicyPattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.ElementCollection;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * Data app, which is distributed via the App Store and can be deployed inside the Connector.
 */
@javax.persistence.Entity
@Table(name = "app")
@SQLDelete(sql = "UPDATE app SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DataApp extends NamedEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * Text documentation of the data app.
     */
    private String appDocumentation;

    /**
     * Endpoints provided by the data app.
     */
    @OneToMany
    private List<AppEndpoint> appEndpoints;

    /**
     * Environment variables of the data app.
     */
    private String appEnvironmentVariables;

    /**
     * Storage configuration of the data app (e.g. path in the file system or volume name).
     */
    private String appStorageConfiguration;

    /**
     * Usage policy patterns supported by the data app.
     */
    @ElementCollection
    private List<PolicyPattern> supportedUsagePolicies;

}
