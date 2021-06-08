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
package io.dataspaceconnector.model;

import io.dataspaceconnector.utils.MetadataUtils;
import org.springframework.stereotype.Component;

/**
 * Creates and updates generic endpoints.
 */
@Component
public class GenericEndpointFactory extends EndpointFactory<GenericEndpoint, GenericEndpointDesc> {

    /**
     * Default absolute path.
     */
    private static final String DEFAULT_PATH = "https://path";

    /**
     * @param desc The description passed to the factory.
     * @return The app endpoint entity.
     */
    @Override
    protected GenericEndpoint createInternal(final GenericEndpointDesc desc) {
        return new GenericEndpoint();
    }

    /**
     * @param genericEndpoint The app endpoint.
     * @param desc            The description of the new entity.
     * @return True, if app endpoint is updated.
     */
    @Override
    protected boolean updateInternal(final GenericEndpoint genericEndpoint,
                                     final GenericEndpointDesc desc) {
        return updateAbsolutePath(genericEndpoint, desc.getAbsolutePath());
    }

    /**
     * @param genericEndpoint The generic endpoint.
     * @param absolutePath    The absolute path of the generic endpoint.
     * @return True, if generic endpoint is updated.
     */
    private boolean updateAbsolutePath(final GenericEndpoint genericEndpoint,
                                       final String absolutePath) {
        final var newAbsolutePath =
                MetadataUtils.updateString(genericEndpoint.getAbsolutePath(), absolutePath,
                        DEFAULT_PATH);
        newAbsolutePath.ifPresent(genericEndpoint::setAbsolutePath);

        return newAbsolutePath.isPresent();
    }
}
