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

import org.springframework.stereotype.Component;

/**
 * Creates and updates an app endpoint.
 */
@Component
public class AppEndpointFactory extends EndpointFactory<AppEndpointImpl, AppEndpointDesc> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AppEndpointImpl initializeEntity(final AppEndpointDesc desc) {
        final var appEndpoint = new AppEndpointImpl();
        appEndpoint.setEndpointPort(desc.getEndpointPort());
        appEndpoint.setEndpointType(desc.getEndpointType());
        appEndpoint.setLanguage(desc.getLanguage());
        appEndpoint.setProtocol(desc.getProtocol());
        appEndpoint.setMediaType(desc.getMediaType());
        appEndpoint.setDocs(desc.getDocs());
        appEndpoint.setInfo(desc.getInfo());
        appEndpoint.setPath(desc.getPath());

        return appEndpoint;
    }

    /**
     * Sets the external exposed ports of an AppEndpoint.
     *
     * @param appEndpoint The app endpoint.
     * @param exposedPort The exposed port information
     * @return The app endpoint with external port information.
     */
    public AppEndpointImpl setExternalPort(final AppEndpointImpl appEndpoint,
                                           final int exposedPort) {
        appEndpoint.setExposedPort(exposedPort);
        return appEndpoint;
    }

    /**
     * Sets the location of an AppEndpoint.
     *
     * @param appEndpoint The app endpoint.
     * @param location The location URI (access URL) of the AppEndpoint.
     * @return The app endpoint with updated location URI.
     */
    public AppEndpointImpl setLocation(final AppEndpointImpl appEndpoint, final String location) {
        appEndpoint.setLocation(location);
        return appEndpoint;
    }
}
