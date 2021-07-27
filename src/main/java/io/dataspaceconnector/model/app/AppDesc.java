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

import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.named.NamedDescription;
import io.dataspaceconnector.service.usagecontrol.PolicyPattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URI;
import java.util.List;


/**
 * Describes a data app. Use this structure to create
 * or update a data app.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class AppDesc extends NamedDescription {

    //App attributes

    /**
     * Text documentation of the data app.
     */
    private String appDocumentation;

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
    private List<PolicyPattern> supportedUsagePolicies;

    //Resource attributes

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
     * The endpoint of the resource.
     */
    private URI endpointDocumentation;

    //Representation attributes

    /**
     * Distribution service, where the represented app can be downloaded.
     */
    private URI dataAppDistributionService;

    /**
     * "Runtime environment of a data app, e.g., software (or hardware) required to run the app.
     */
    private String dataAppRuntimeEnvironment;

    //Artifact attributes

    /**
     * The artifact id on provider side.
     */
    private URI remoteId;

    /**
     * The provider's address for artifact request messages.
     */
    private URI remoteAddress;

    /**
     * The data.
     */
    private LocalData data;
}
