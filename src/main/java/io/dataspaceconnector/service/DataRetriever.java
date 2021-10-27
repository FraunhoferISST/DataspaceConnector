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
package io.dataspaceconnector.service;

import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.common.net.ApiReferenceHelper;
import io.dataspaceconnector.common.net.HttpAuthentication;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.RouteDataRetriever;
import io.dataspaceconnector.common.routing.dataretrieval.DataRetrievalService;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Retrieves data from the local database, remote HTTP services and Camel routes.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class DataRetriever {

    /**
     * Service for http communication.
     **/
    private final @NonNull HttpService httpSvc;

    /**
     * Retrieves data using Camel routes.
     */
    private final @NonNull RouteDataRetriever routeRetriever;

    /**
     * Helper class for managing API endpoint references.
     */
    private final @NonNull ApiReferenceHelper apiReferenceHelper;

    /**
     * Retrieves the data for an artifact using the specified query input.
     *
     * @param artifact   The artifact.
     * @param queryInput The query input.
     * @return The data.
     * @throws IOException if the data cannot be retrieved.
     */
    public InputStream retrieveData(final ArtifactImpl artifact, final QueryInput queryInput)
            throws IOException {
        return getDataFromInternalDB(artifact, queryInput);
    }

    /**
     * Get the data from the internal database. No policy enforcement is performed here!
     *
     * @param artifact   The artifact which data should be returned.
     * @param queryInput The query input.
     * @return The artifact's data.
     * @throws IOException if the data cannot be received.
     */
    private InputStream getDataFromInternalDB(final ArtifactImpl artifact,
                                             final QueryInput queryInput) throws IOException {
        final var data = artifact.getData();

        InputStream rawData;
        if (data instanceof LocalData) {
            rawData = getData((LocalData) data);
        } else if (data instanceof RemoteData) {
            rawData = getData((RemoteData) data, queryInput);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("Unknown data type. [artifactId=({})]", artifact.getId());
            }
            throw new UnreachableLineException("Unknown data type.");
        }

        return rawData;
    }

    /**
     * Get local data.
     *
     * @param data The data container.
     * @return The stored data.
     */
    private InputStream getData(final LocalData data) {
        return toInputStream(data.getValue());
    }

    private InputStream toInputStream(final byte[] data) {
        if (data == null) {
            return ByteArrayInputStream.nullInputStream();
        } else {
            return new ByteArrayInputStream(data);
        }
    }

    /**
     * Get remote data.
     *
     * @param data       The data container.
     * @param queryInput The query input.
     * @return The stored data.
     * @throws IOException if IO errors occur.
     */
    private InputStream getData(final RemoteData data, final QueryInput queryInput)
            throws IOException {
        try {
            return downloadDataFromBackend(data, queryInput);
        } catch (IOException | DataRetrievalException e) {
            if (log.isWarnEnabled()) {
                log.warn("Could not connect to data source. [exception=({})]", e.getMessage(), e);
            }

            throw new IOException("Could not connect to data source.", e);
        }
    }

    private InputStream downloadDataFromBackend(final RemoteData data, final QueryInput queryInput)
            throws IOException {
        InputStream backendData;
        if (apiReferenceHelper.isRouteReference(data.getAccessUrl())) {
            backendData = getData(routeRetriever, data.getAccessUrl(), queryInput);
        } else {
            if (!data.getAuthentication().isEmpty()) {
                backendData = getData(httpSvc, data.getAccessUrl(), queryInput,
                        data.getAuthentication());
            } else {
                backendData = getData(httpSvc, data.getAccessUrl(), queryInput);
            }
        }

        return backendData;
    }

    private InputStream getData(final DataRetrievalService service, final URL target,
                                final QueryInput queryInput)
            throws IOException, DataRetrievalException {
        return service.get(target, queryInput).getData();
    }

    private InputStream getData(final DataRetrievalService service, final URL target,
                                final QueryInput queryInput,
                                final List<? extends HttpAuthentication> authentications)
            throws IOException, DataRetrievalException {
        return service.get(target, queryInput, authentications).getData();
    }

}
