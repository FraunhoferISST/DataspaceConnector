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
package io.dataspaceconnector.model.endpoint;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The endpoint factory proxy class.
 */
@RequiredArgsConstructor
public class EndpointFactoryProxy extends EndpointFactory<Endpoint, EndpointDesc> {

    /**
     * The factory for the generic endpoint.
     */
    private final @NonNull GenericEndpointFactory generic;

    @Override
    protected final Endpoint initializeEntity(final EndpointDesc desc) {
        assert desc instanceof GenericEndpointDesc;
        return generic.initializeEntity((GenericEndpointDesc) desc);
    }

    @Override
    protected final boolean updateInternal(final Endpoint endpoint, final EndpointDesc desc) {
        assert endpoint instanceof GenericEndpoint && desc instanceof GenericEndpointDesc;
        return generic.updateInternal((GenericEndpoint) endpoint, (GenericEndpointDesc) desc);
    }
}
