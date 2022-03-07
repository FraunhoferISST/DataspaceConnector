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
package io.dataspaceconnector.model.artifact;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.named.NamedDescription;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.net.URI;
import java.net.URL;

/**
 * A description of an artifact.
 * This class is consumed when creating or updating an artifact.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ArtifactDesc extends NamedDescription {

    /**
     * The agreement id on provider side.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteId;

    /**
     * The provider's address for artifact request messages.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private URI remoteAddress;

    /**
     * The url of the data location.
     */
    private URL accessUrl;

    /**
     * The username for authentication at the data location.
     */
    private AuthenticationDesc basicAuth;

    /**
     * The API key name used for authentication at the data location.
     */
    private AuthenticationDesc apiKey;

    /**
     * Some value for storing data locally.
     */
    private String value;

    /**
     * Indicates whether the artifact should be downloaded automatically.
     */
    private boolean automatedDownload = ArtifactFactory.DEFAULT_AUTO_DOWNLOAD;
}
