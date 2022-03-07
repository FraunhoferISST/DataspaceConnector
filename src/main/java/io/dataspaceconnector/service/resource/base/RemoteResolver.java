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
package io.dataspaceconnector.service.resource.base;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * Resolves an remote address to an local entity.
 */
public interface RemoteResolver {
    /**
     * Search for an local entity by its remote id.
     *
     * @param remoteId The remote id.
     * @return The local entity id.
     */
    Optional<UUID> identifyByRemoteId(URI remoteId);
}
