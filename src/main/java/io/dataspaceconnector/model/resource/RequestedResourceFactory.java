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
package io.dataspaceconnector.model.resource;

import io.dataspaceconnector.model.util.FactoryUtils;

import java.net.URI;
import java.util.List;

/**
 * Creates and updates a resource.
 */
public final class RequestedResourceFactory
        extends ResourceFactory<RequestedResource, RequestedResourceDesc> {

    /**
     * The default remote id assigned to all requested resources.
     */
    public static final URI DEFAULT_REMOTE_ID = URI.create("genesis");

    @Override
    protected RequestedResource createInternal(final RequestedResourceDesc desc) {
        return new RequestedResource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSamples(final Resource resource, final List<URI> samples) {
        // Nothing to do here.
    }

    @Override
    protected boolean updateInternal(final RequestedResource resource,
                                     final RequestedResourceDesc desc) {
        final var hasParentUpdated = super.updateInternal(resource, desc);
        final var hasRemoteIdUpdated = updateRemoteId(resource, desc.getRemoteId());
        return hasParentUpdated || hasRemoteIdUpdated;
    }

    private boolean updateRemoteId(final RequestedResource resource, final URI remoteId) {
        final var newUri = FactoryUtils.updateUri(resource.getRemoteId(), remoteId,
                DEFAULT_REMOTE_ID);
        newUri.ifPresent(resource::setRemoteId);

        return newUri.isPresent();
    }
}
