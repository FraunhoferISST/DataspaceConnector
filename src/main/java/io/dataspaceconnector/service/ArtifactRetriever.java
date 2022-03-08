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
package io.dataspaceconnector.service;

import io.dataspaceconnector.common.net.QueryInput;

import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

/**
 * Performs an artifact request for an artifact.
 */
public interface ArtifactRetriever {

    /**
     * Perform an artifact request for a given artifact.
     *
     * @param artifactId       The artifact whose data should be updated.
     * @param recipient        The target connector holding the artifact's data.
     * @param transferContract The contract authorizing the data transfer.
     * @return The artifact's data.
     */
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract);

    /**
     * Perform an artifact request for a given artifact with query parameters.
     *
     * @param artifactId       The artifact whose data should be updated.
     * @param recipient        The target connector holding the artifact's data.
     * @param transferContract The contract authorizing the data transfer.
     * @param query            The data query for specifying the requested data.
     * @return The artifact's data.
     */
    InputStream retrieve(UUID artifactId, URI recipient, URI transferContract, QueryInput query);

}
