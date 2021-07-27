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
package io.dataspaceconnector.view.app;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.service.usagecontrol.PolicyPattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * A DTO for controlled exposing of app information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = "apps", itemRelation = "app")
public class AppView extends RepresentationModel<AppView> {

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
     * Text documentation of the data app.
     */
    private String appDocumentation;

    /**
     * The keywords of the resource.
     */
    private List<String> keywords;

    /**
     * The publisher of the resource.
     */
    private URI publisher;

    /**
     * The owner of the resource.
     */
    private URI sovereign;

    /**
     * The language of the resource.
     */
    private String language;

    /**
     * The license of the resource.
     */
    private URI license;

    /**
     * Environment variables of the data app.
     */
    private String appEnvironmentVariables;

    /**
     * Storage configuration of the data app (e.g. path in the file system or volume name).
     */
    private String appStorageConfiguration;

    /**
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;

    /**
     * Distribution service, where the represented app can be downloaded.
     */
    private URI dataAppDistributionService;

    /**
     * "Runtime environment of a data app, e.g., software (or hardware) required to run the app.
     */
    private String dataAppRuntimeEnvironment;

    /**
     * The artifact id on provider side.
     */
    private URI remoteId;

    /**
     * The provider's address for artifact request messages.
     */
    private URI remoteAddress;

    /**
     * The version of the resource.
     */
    private long version;

    /**
     * Usage policy patterns supported by the data app.
     */
    private List<PolicyPattern> supportedUsagePolicies;

}
