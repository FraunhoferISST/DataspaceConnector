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

import io.dataspaceconnector.utils.ErrorMessages;
import io.dataspaceconnector.utils.MetadataUtils;
import io.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Objects;

/**
 * Creates and updates an app endpoint.
 */
@Component
public class AppEndpointFactory extends EndpointFactory<AppEndpoint, AppEndpointDesc> {

    /**
     * The default access url.
     */
    public static final String DEFAULT_ACCESS_URL = "https://default";

    /**
     *
     */
    public static final String DEFAULT_STRING = "unknown";

    /**
     *
     */
    public static final String DEFAULT_MEDIATYPE = "mediatype";

    /**
     * @param desc The description passed to the factory.
     * @return The app endpoint entity.
     */
    @Override
    protected AppEndpoint createInternal(final AppEndpointDesc desc) {
        return new AppEndpoint();
    }

    /**
     * @param appEndpoint The app endpoint.
     * @param desc        The description of the new entity.
     * @return True, if app endpoint is updated.
     */
    @Override
    protected boolean updateInternal(final AppEndpoint appEndpoint, final AppEndpointDesc desc) {
        Utils.requireNonNull(appEndpoint, ErrorMessages.ENTITY_NULL);
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var hasUpdatedAccessUrl = updateAccessUrl(appEndpoint, desc.getAccessURL());
        final var hasUpdatedMediaType = updateMediaType(appEndpoint, desc.getMediaType());
        final var hasUpdatedPort = updateEndpointPort(appEndpoint, desc.getAppEndpointPort());
        final var hasUpdatedProtocol = updateProtocol(appEndpoint, desc.getAppEndpointProtocol());
        final var hasUpdatedLanguage = updateLanguage(appEndpoint, desc.getLanguage());
        final var hasUpdatedAppEndpointType = updateAppEndpointType(appEndpoint,
                desc.getAppEndpointType());

        return hasUpdatedAccessUrl || hasUpdatedMediaType || hasUpdatedPort
                || hasUpdatedProtocol || hasUpdatedLanguage || hasUpdatedAppEndpointType;
    }

    /**
     * @param appEndpoint     The app endpoint.
     * @param appEndpointType The new app endpoint type.
     * @return True, if app endpoint is updated.
     */
    private boolean updateAppEndpointType(final AppEndpoint appEndpoint,
                                          final AppEndpointType appEndpointType) {
        appEndpoint.setAppEndpointType(Objects.requireNonNullElse(appEndpointType,
                AppEndpointType.DEFAULT_ENDPOINT));
        return true;
    }

    /**
     * @param appEndpoint The app endpoint.
     * @param language    The app endpoint protocol.
     * @return True, if language is updated.
     */
    private boolean updateLanguage(final AppEndpoint appEndpoint, final String language) {
        final var newLanguage = MetadataUtils.updateString(appEndpoint.getAppEndpointProtocol(),
                language, "EN");
        newLanguage.ifPresent(appEndpoint::setLanguage);
        return newLanguage.isPresent();
    }

    /**
     * @param appEndpoint         The app endpoint.
     * @param appEndpointProtocol The app endpoint protocol.
     * @return True, if protocol is updated
     */
    private boolean updateProtocol(final AppEndpoint appEndpoint,
                                   final String appEndpointProtocol) {
        final var newProtocol =
                MetadataUtils.updateString(appEndpoint.getAppEndpointProtocol(),
                        appEndpointProtocol, DEFAULT_STRING);
        newProtocol.ifPresent(appEndpoint::setAppEndpointProtocol);
        return newProtocol.isPresent();
    }

    /**
     * @param appEndpoint     The app endpoint.
     * @param appEndpointPort The new app endpoint port.
     * @return True, if app endpoint is updated.
     */
    private boolean updateEndpointPort(final AppEndpoint appEndpoint, final int appEndpointPort) {
        final var newPort = MetadataUtils.updateInteger(appEndpoint.getAppEndpointPort(),
                appEndpointPort);

        if (newPort == appEndpointPort) {
            appEndpoint.setAppEndpointPort(newPort);
            return true;
        }
        return false;
    }

    /**
     * @param appEndpoint The app endpoint.
     * @param mediaType   The new media type.
     * @return True, if media type is updated.
     */
    private boolean updateMediaType(final AppEndpoint appEndpoint, final String mediaType) {
        final var newMediaType = MetadataUtils.updateString(appEndpoint.getMediaType(), mediaType,
                DEFAULT_MEDIATYPE);
        newMediaType.ifPresent(appEndpoint::setMediaType);
        return newMediaType.isPresent();
    }

    /**
     * @param appEndpoint The app endpoint
     * @param accessURL   The new access url
     * @return true, if access url is updated
     */
    private boolean updateAccessUrl(final AppEndpoint appEndpoint, final URI accessURL) {
        final var newUri = MetadataUtils.updateUri(appEndpoint.getName(), accessURL,
                                                   URI.create(DEFAULT_ACCESS_URL));

        newUri.ifPresent(appEndpoint::setName);
        return newUri.isPresent();
    }
}
