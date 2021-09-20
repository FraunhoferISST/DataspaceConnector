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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.BasicAuthentication;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.ConnectorEndpointBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.endpoint.Endpoint;
import io.dataspaceconnector.model.endpoint.GenericEndpoint;
import io.dataspaceconnector.service.resource.ids.builder.base.AbstractIdsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Converts dsc endpoints to ids endpoints.
 */
@Component
public final class IdsEndpointBuilder
        extends AbstractIdsBuilder<Endpoint, de.fraunhofer.iais.eis.Endpoint> {

    /**
     * Constructs an IdsEndpointBuilder.
     *
     * @param selfLinkHelper the self link helper.
     */
    @Autowired
    public IdsEndpointBuilder(final SelfLinkHelper selfLinkHelper) {
        super(selfLinkHelper);
    }

    @Override
    protected de.fraunhofer.iais.eis.Endpoint createInternal(
            final Endpoint endpoint, final int currentDepth, final int maxDepth)
            throws ConstraintViolationException {

        final var documentation = endpoint.getDocs();
        final var location = endpoint.getLocation();
        final var info = new TypedLiteral(endpoint.getInfo(), "EN");

        de.fraunhofer.iais.eis.Endpoint idsEndpoint;
        if (endpoint instanceof GenericEndpoint) {

            final var genericEndpoint = (GenericEndpoint) endpoint;
            BasicAuthentication basicAuth = null;
            if (genericEndpoint.getDataSource() != null
                    && genericEndpoint.getDataSource().getAuthentication() != null) {
                final var auth = (BasicAuth) genericEndpoint.getDataSource().getAuthentication();
                basicAuth = new BasicAuthenticationBuilder()
                        ._authUsername_(auth.getUsername())
                        ._authPassword_(auth.getPassword())
                        .build();
            }

            idsEndpoint = new GenericEndpointBuilder(getAbsoluteSelfLink(endpoint))
                    ._accessURL_(location)
                    ._genericEndpointAuthentication_(basicAuth)
                    ._endpointDocumentation_(Util.asList(documentation))
                    ._endpointInformation_(Util.asList(info))
                    .build();
        } else {
            idsEndpoint = new ConnectorEndpointBuilder(getAbsoluteSelfLink(endpoint))
                    ._accessURL_(location)
                    ._endpointDocumentation_(Util.asList(documentation))
                    ._endpointInformation_(Util.asList(info))
                    .build();
        }

        return idsEndpoint;
    }

}
