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
package io.dataspaceconnector.model.routes;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;

import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.configurations.DeployMethod;
import io.dataspaceconnector.model.endpoints.Endpoint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 *
 */
@javax.persistence.Entity
@Table(name = "route")
@SQLDelete(sql = "UPDATE route SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class Route extends Entity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The deploy method of the route.
     */
    @Enumerated(EnumType.STRING)
    private DeployMethod deploy;

    /**
     * List of subroutes.
     */
    @OneToMany
    private List<Route> steps;

    /**
     * The route configuration.
     */
    private String config;

    /**
     * The start endpoint of the route.
     */
    @OneToOne
    private Endpoint start;

    /**
     * The last endpoint of the route.
     */
    @OneToOne
    private Endpoint end;

    /**
     * List of offered resources.
     */
    @OneToMany
    private List<Artifact> output;
}
