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
package io.dataspaceconnector.service.resource.ids.builder;

import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;

import de.fraunhofer.iais.eis.AppEndpointBuilder;
import de.fraunhofer.iais.eis.AppEndpointType;
import de.fraunhofer.iais.eis.BasicAuthentication;
import de.fraunhofer.iais.eis.BasicAuthenticationBuilder;
import de.fraunhofer.iais.eis.GenericEndpointBuilder;
import de.fraunhofer.iais.eis.IANAMediaTypeBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.common.ids.mapping.ToIdsObjectMapper;
import io.dataspaceconnector.model.auth.ApiKey;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.datasource.DatabaseDataSource;
import io.dataspaceconnector.model.endpoint.AppEndpoint;
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
        var location = endpoint.getLocation();
        final var info = new TypedLiteral(endpoint.getInfo(), "EN");

        URI accessUrl;
        try {
            accessUrl = URI.create(location);
        } catch (IllegalArgumentException exception) {
            accessUrl = URI.create("https://default-url");
        }

        de.fraunhofer.iais.eis.Endpoint idsEndpoint;
        if (endpoint instanceof GenericEndpoint) {

            final var genericEndpoint = (GenericEndpoint) endpoint;
            BasicAuthentication idsAuth = null;
            final var additional = new HashMap<String, String>();
            if (genericEndpoint.getDataSource() != null
                    && genericEndpoint.getDataSource().getAuthentication() != null) {

                final var dataSource = genericEndpoint.getDataSource();
                final var auth = dataSource.getAuthentication();

                if (auth instanceof BasicAuth) {
                    final var basicAuth = (BasicAuth) auth;
                    idsAuth = new BasicAuthenticationBuilder()
                            ._authUsername_(basicAuth.getUsername())
                            ._authPassword_(basicAuth.getPassword())
                            .build();
                    idsAuth.setProperty("type", "basic");
                } else {
                    final var apiKeyAuth = (ApiKey) auth;
                    idsAuth = new BasicAuthenticationBuilder()
                            ._authUsername_(apiKeyAuth.getKey())
                            ._authPassword_(apiKeyAuth.getValue())
                            .build();
                    idsAuth.setProperty("type", "api-key");
                }

                if (dataSource instanceof DatabaseDataSource) {
                    additional.put("database", "true");

                    if (location.contains("?")) {
                        location = location.concat("&dataSource=#" + dataSource.getId());
                    } else {
                        location = location.concat("?dataSource=#" + dataSource.getId());
                    }
                }
            }

            idsEndpoint = new GenericEndpointBuilder(getAbsoluteSelfLink(endpoint))
                    ._path_(location)
                    ._accessURL_(accessUrl)
                    ._genericEndpointAuthentication_(idsAuth)
                    ._endpointDocumentation_(Util.asList(documentation))
                    ._endpointInformation_(Util.asList(info))
                    .build();
            additional.forEach(idsEndpoint::setProperty);

        } else if (endpoint instanceof AppEndpoint) {

            final var appEndpoint = (AppEndpoint) endpoint;

            idsEndpoint = new AppEndpointBuilder(getAbsoluteSelfLink(endpoint))
                    ._path_(location)
                    ._accessURL_(accessUrl)
                    ._endpointDocumentation_(Util.asList(documentation))
                    ._endpointInformation_(Util.asList(info))
                    ._appEndpointType_(AppEndpointType.valueOf(appEndpoint.getEndpointType()))
                    ._appEndpointPort_(BigInteger.valueOf(appEndpoint.getEndpointPort()))
                    ._appEndpointProtocol_(appEndpoint.getProtocol())
                    ._appEndpointMediaType_(new IANAMediaTypeBuilder()
                            ._filenameExtension_(appEndpoint.getMediaType())
                            .build())
                    ._language_(ToIdsObjectMapper.getLanguage(appEndpoint.getLanguage()))
                    .build();

        } else {
            throw new UnreachableLineException(ErrorMessage.UNKNOWN_TYPE);
        }

        return idsEndpoint;
    }

}
