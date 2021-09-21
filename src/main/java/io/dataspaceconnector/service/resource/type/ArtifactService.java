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
package io.dataspaceconnector.service.resource.type;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.common.exception.DataDispatchException;
import io.dataspaceconnector.common.exception.DataRetrievalException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.exception.ResourceNotFoundException;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.common.dataretrieval.DataRetrievalService;
import io.dataspaceconnector.common.net.HttpAuthentication;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.net.RetrievalInformation;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.common.usagecontrol.AccessVerificationInput;
import io.dataspaceconnector.common.usagecontrol.PolicyVerifier;
import io.dataspaceconnector.common.usagecontrol.VerificationResult;
import io.dataspaceconnector.common.util.UUIDUtils;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.config.BasePath;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.ArtifactRetriever;
import io.dataspaceconnector.common.routing.RouteDataRetriever;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.base.RemoteResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Handles the basic logic for artifacts.
 */
@Log4j2
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class ArtifactService extends BaseEntityService<Artifact, ArtifactDesc>
        implements RemoteResolver {

    /**
     * Repository for storing data.
     **/
    private final @NonNull DataRepository dataRepo;

    /**
     * Service for http communication.
     **/
    private final @NonNull HttpService httpSvc;

    /**
     * Repository for storing AuthTypes.
     */
    private final @NonNull AuthenticationRepository authRepo;

    /**
     * Repository for storing routes.
     */
    private final @NonNull RouteService routeSvc;

    /**
     * Retrieves data using Camel routes.
     */
    private final @NonNull RouteDataRetriever routeRetriever;

    /**
     * Dispatches data using Camel routes.
     */
    private final @NonNull RouteDataDispatcher routeDispatcher;

    /**
     * Constructor for ArtifactService.
     *
     * @param repository               The artifact repository.
     * @param factory                  The artifact logic repository.
     * @param dataRepository           The data repository.
     * @param httpService              The HTTP service for fetching remote data.
     * @param authenticationRepository The AuthType repository.
     * @param routeService             The Route service.
     * @param routeDataRetriever       The route data retriever.
     * @param routeDataDispatcher      The route data dispatcher.
     */
    public ArtifactService(final BaseEntityRepository<Artifact> repository,
                           final AbstractFactory<Artifact, ArtifactDesc> factory,
                           final @NonNull DataRepository dataRepository,
                           final @NonNull HttpService httpService,
                           final @NonNull AuthenticationRepository authenticationRepository,
                           final @NonNull RouteService routeService,
                           final @NonNull RouteDataRetriever routeDataRetriever,
                           final @NonNull RouteDataDispatcher routeDataDispatcher) {
        super(repository, factory);
        this.dataRepo = dataRepository;
        this.httpSvc = httpService;
        this.authRepo = authenticationRepository;
        this.routeSvc = routeService;
        this.routeRetriever = routeDataRetriever;
        this.routeDispatcher = routeDataDispatcher;
    }

    /**
     * Persist the artifact and its data.
     *
     * @param artifact The artifact to persists.
     * @return The persisted artifact.
     */
    @Override
    protected Artifact persist(final Artifact artifact) {
        final var tmp = (ArtifactImpl) artifact;
        if (tmp.getData() != null) {
            if (tmp.getData().getId() == null) {
                // The data element is new, insert
                if (tmp.getData() instanceof RemoteData) {
                    var data = (RemoteData) tmp.getData();
                    data.getAuthentication().forEach(authRepo::saveAndFlush);
                }
                dataRepo.saveAndFlush(tmp.getData());
            } else {
                // The data element exists already, check if an update is
                // required
                final var storedCopy = dataRepo.getById(tmp.getData().getId());
                if (!storedCopy.equals(tmp.getData())) {
                    dataRepo.saveAndFlush(tmp.getData());
                }
            }

            if (tmp.getData() instanceof LocalData) {
                final var factory = (ArtifactFactory) getFactory();
                factory.updateByteSize(artifact, ((LocalData) tmp.getData()).getValue());
            } else if (tmp.getData() instanceof RemoteData) {
                return checkAndPersistRoute(tmp);
            }
        }

        return super.persist(tmp);
    }

    /**
     * Persists an artifact with remote data. If the access URL is a route reference, the
     * corresponding route is read from the database. If that route is already linked to an
     * artifact, an {@link InvalidEntityException} is thrown. Else, the artifact is linked to
     * the route after it has been persisted.
     *
     * @param artifact the artifact to persist.
     * @return the persisted artifact.
     * @throws InvalidEntityException if the access URL is not a valid URI or the referenced route
     *                                is already linked to an artifact.
     */
    private Artifact checkAndPersistRoute(final ArtifactImpl artifact) {
        final var url = ((RemoteData) artifact.getData()).getAccessUrl();
        try {
            if (url.toString().startsWith(getRoutesApiUrl())) {
                final var routeId = UUIDUtils.uuidFromUri(url.toURI());
                final var route = routeSvc.get(routeId);

                if (route.getOutput() != null) {
                    throw new InvalidEntityException("Referenced route is already linked to "
                            + "an artifact.");
                }

                final var persisted = super.persist(artifact);
                routeSvc.setOutput(routeId, persisted.getId());
                return persisted;
            } else {
                return super.persist(artifact);
            }
        } catch (URISyntaxException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Route ID in access URL of artifact is not a valid URI. "
                        + "[accessUrl=({}), exception=({})]", url, exception.getMessage(),
                        exception);
            }
            throw new InvalidEntityException("Route ID in access URL of artifact is not a "
                    + "valid URI.");
        } catch (ResourceNotFoundException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Could not find route matching the access URL. "
                                + "[accessUrl=({})]", url, exception);
            }
            throw new InvalidEntityException("Could not find route matching the access URL.");
        }
    }

    /**
     * Get the artifacts data. If agreements for this resource exist, all of them will be tried for
     * data access.
     *
     * @param accessVerifier Checks if the data access should be allowed.
     * @param retriever      Retrieves the data from an external source.
     * @param artifactId     The id of the artifact.
     * @param queryInput     The query for the backend.
     * @param routeIds       The routes the data should be sent to.
     * @return The artifacts data.
     * @throws PolicyRestrictionException                                       if the data
     * access has been denied.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the artifact does
     *                                                                          not exist.
     * @throws IllegalArgumentException                                         if any of the
     * parameters is null.
     * @throws IOException                                                      if IO errors occur.
     */
    public InputStream getData(final PolicyVerifier<AccessVerificationInput> accessVerifier,
                               final ArtifactRetriever retriever, final UUID artifactId,
                               final QueryInput queryInput, final List<URI> routeIds)
            throws PolicyRestrictionException, IOException {
        final var agreements =
                ((ArtifactRepository) getRepository()).findRemoteOriginAgreements(artifactId);
        if (agreements.size() > 0) {
            return tryToAccessDataByUsingAnyAgreement(accessVerifier, retriever, artifactId,
                    queryInput, agreements, routeIds);
        }

        // The artifact is not assigned to any requested resources. It must be offered if it exists.
        var data = new BackendDataService(routeRetriever, httpSvc, queryInput)
                .getDataFromInternalDB((ArtifactImpl) get(artifactId));
        return new DataDispatcher(routeIds, data).dispatch();
    }

    private InputStream tryToAccessDataByUsingAnyAgreement(
            final PolicyVerifier<AccessVerificationInput> accessVerifier,
            final ArtifactRetriever retriever, final UUID artifactId, final QueryInput queryInput,
            final List<URI> agreements, final List<URI> routeIds) throws IOException {
        /*
         * NOTE: Check if agreements with remoteIds are set for this artifact. If such agreements
         * exist the artifact must be assigned to a requested resource. The data access should
         * now be treated from the perspective of the data consumer. Since no knowledge which
         * agreement applies has been passed we need to query the database for all viable agreements
         * and try accessing the data till one of them returns the data. If none of them returns
         * the data it means all data access has been forbidden. Do not proceed.
         */

        var policyException = new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        for (final var agRemoteId : agreements) {
            try {
                final var info = new RetrievalInformation(agRemoteId, null, queryInput);
                return getData(accessVerifier, retriever, artifactId, info, routeIds);
            } catch (PolicyRestrictionException exception) {
                // Access denied, log it and try the next agreement.
                if (log.isDebugEnabled()) {
                    log.debug("Tried to access artifact data by trying an agreement. "
                                    + "[artifactId=({}), agreementId=({})]",
                            artifactId, agRemoteId);
                }

                policyException = exception;
            }
        }

        // All attempts on accessing data failed. Deny access with the last rejection reason.
        if (log.isDebugEnabled()) {
            log.debug("The requested resource is not owned by this connector."
                    + " Access forbidden. [artifactId=({})]", artifactId);
        }

        throw policyException;
    }

    /**
     * Get data restricted by a contract. If the data is not available an artifact requests will
     * pull the data.
     *
     * @param accessVerifier Checks if the data access should be allowed.
     * @param retriever      Retrieves the data from an external source.
     * @param artifactId     The id of the artifact.
     * @param information    Information for pulling the data from a remote source.
     * @param routeIds       The routes the data should be sent to.
     * @return The artifact's data.
     * @throws PolicyRestrictionException                                       if the data
     * access has been denied.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the artifact does
     *                                                                          not exist.
     * @throws IllegalArgumentException                                         if any of the
     * parameters is null.
     * @throws IOException                                                      if IO errors occurr.
     */
    public InputStream getData(final PolicyVerifier<AccessVerificationInput> accessVerifier,
                               final ArtifactRetriever retriever, final UUID artifactId,
                               final RetrievalInformation information, final List<URI> routeIds)
            throws PolicyRestrictionException, IOException {
        // Check the artifact exists and access is granted.
        final var artifact = get(artifactId);
        verifyDataAccess(accessVerifier,
                new AccessVerificationInput(information.getTransferContract(), artifact));

        // Make sure the data exists and is up to date.
        if (shouldDownload(artifact, information)) {
            final var data = downloadAndUpdateData(retriever, artifactId, information, artifact,
                    routeIds);
            incrementAccessCounter(artifact);
            return data;
        }

        // Artifact exists, access granted, data exists and data up to date.
        var data = new BackendDataService(routeRetriever, httpSvc, information.getQueryInput())
                .getDataFromInternalDB((ArtifactImpl) artifact);
        return new DataDispatcher(routeIds, data).dispatch();
    }

    private void verifyDataAccess(final PolicyVerifier<AccessVerificationInput> accessVerifier,
                                  final AccessVerificationInput verificationInput) {
        if (accessVerifier.verify(verificationInput) == VerificationResult.DENIED) {
            if (log.isInfoEnabled()) {
                log.info("Access denied. [artifactId=({})]",
                        verificationInput.getArtifact().getId());
            }

            throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        }
    }

    private InputStream downloadAndUpdateData(final ArtifactRetriever retriever,
                                              final UUID artifactId,
                                              final RetrievalInformation information,
                                              final Artifact artifact, final List<URI> routeIds)
            throws IOException {
        final var dataStream = retriever.retrieve(artifactId,
                artifact.getRemoteAddress(),
                information.getTransferContract(),
                information.getQueryInput());

        if (routeIds != null && !routeIds.isEmpty()) {
            return new DataDispatcher(routeIds, dataStream).dispatch();
        } else {
            return setData(artifactId, dataStream);
        }
    }

    private void incrementAccessCounter(final Artifact artifact) {
        artifact.incrementAccessCounter();
        persist(artifact);
    }

    private boolean shouldDownload(final Artifact artifact,
                                   final RetrievalInformation information) {
        if (information.getForceDownload() == null && information.getQueryInput() == null) {
            return !isDataPresent(artifact) || artifact.isAutomatedDownload();
        } else {
            return true;
        }
    }

    private boolean isDataPresent(final Artifact artifact) {
        if (artifact.getAdditional().containsKey("ids:byteSize")) {
            final var providerDataSize =
                    Integer.parseInt(artifact.getAdditional().get("ids:byteSize"));
            final var thisDataSize = artifact.getByteSize();
            return thisDataSize >= providerDataSize;
        }

        return false;
    }

    /**
     * Finds all artifacts referenced in a specific agreement.
     *
     * @param agreementId ID of the agreement
     * @return list of all artifacts referenced in the agreement
     */
    public List<Artifact> getAllByAgreement(final UUID agreementId) {
        Utils.requireNonNull(agreementId, ErrorMessage.ENTITYID_NULL);
        return ((ArtifactRepository) getRepository()).findAllByAgreement(agreementId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UUID> identifyByRemoteId(final URI remoteId) {
        final var repo = (ArtifactRepository) getRepository();
        return repo.identifyByRemoteId(remoteId);
    }

    /**
     * Update an artifacts underlying data.
     *
     * @param artifactId The artifact which should be updated.
     * @param data       The new data.
     * @return The data stored in the artifact.
     * @throws IOException if the data could not be stored.
     */
    @NonNull
    public InputStream setData(final UUID artifactId, final InputStream data) throws IOException {
        final var artifact = get(artifactId);
        final var currentData = ((ArtifactImpl) artifact).getData();
        if (currentData instanceof LocalData) {
            return setLocalData(artifactId, data, artifact, (LocalData) currentData);
        } else {
            throw new NotImplemented();
        }
    }

    @NonNull
    private ByteArrayInputStream setLocalData(final UUID artifactId,
                                              final InputStream data,
                                              final Artifact artifact,
                                              final LocalData localData)
            throws IOException {
        try {
            // Update the internal database and return the new data.
            final var bytes = data.readAllBytes();
            data.close();
            dataRepo.setLocalData(localData.getId(), bytes);
            if (((ArtifactFactory) getFactory()).updateByteSize(artifact, bytes)) {
                ((ArtifactRepository) getRepository()).setArtifactData(artifactId,
                        artifact.getCheckSum(),
                        artifact.getByteSize());
            }

            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to store data. [artifactId=({}), exception=({})]",
                        artifactId, e.getMessage(), e);
            }

            throw new IOException("Failed to store data.", e);
        }
    }

    /**
     * Deletes an artifact with the given id. If the artifact references a route in its access URL,
     * the artifact is removed from the route before deleting it.
     *
     * @param artifactId The id of the entity.
     * @throws IllegalArgumentException if the passed id is null.
     */
    @Override
    public void delete(final UUID artifactId) {
        Utils.requireNonNull(artifactId, ErrorMessage.ENTITYID_NULL);

        try {
            final var artifact = (ArtifactImpl) get(artifactId);
            if (artifact.getData() instanceof RemoteData) {
                final var url = ((RemoteData) artifact.getData()).getAccessUrl();
                if (url.toString().startsWith(getRoutesApiUrl())) {
                    final var routeId = UUIDUtils.uuidFromUri(url.toURI());
                    routeSvc.removeOutput(routeId);
                }
            }
        } catch (URISyntaxException ignore) {
            // If the access URL is not a valid URI, route and artifact could not have been linked
        }

        getRepository().deleteById(artifactId);
    }

    /**
     * Returns the route associated with an artifact, if any.
     *
     * @param artifactId The artifact ID.
     * @return the associated route, if any. Null otherwise.
     * @throws ResourceNotFoundException if the referenced route is unknown.
     */
    public Route getAssociatedRoute(final UUID artifactId) {
        final var artifact = (ArtifactImpl) get(artifactId);
        return routeSvc.findByOutput(artifact);
    }

    /**
     * Returns the URL to the routes API. Required for checking whether an access URL references
     * a Camel route or a remote data source.
     *
     * @return the URL to the routes API.
     */
    private String getRoutesApiUrl() {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return baseUrl + BasePath.ROUTES;
    }

    @RequiredArgsConstructor
    private class BackendDataService {

        /**
         * Retrieves data using Camel routes.
         */
        private final @NonNull RouteDataRetriever retriever;

        /**
         * Retrieves data using HTTP.
         */
        private final @NonNull HttpService httpSvc;

        /**
         * QueryInput for the request.
         */
        private final QueryInput queryInput;

        /**
         * Get the data from the internal database. No policy enforcement is performed here!
         *
         * @param artifact   The artifact which data should be returned.
         * @return The artifact's data.
         * @throws IOException if the data cannot be received.
         */
        public InputStream getDataFromInternalDB(final ArtifactImpl artifact) throws IOException {
            final var data = artifact.getData();

            InputStream rawData;
            if (data instanceof LocalData) {
                rawData = getData((LocalData) data);
            } else if (data instanceof RemoteData) {
                rawData = getData((RemoteData) data);
            } else {
                throw new UnreachableLineException("Unknown data type.");
            }

            incrementAccessCounter(artifact);

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
            return new ByteArrayInputStream(data);
        }

        /**
         * Get remote data.
         *
         * @param data       The data container.
         * @return The stored data.
         * @throws IOException if IO errors occur.
         */
        private InputStream getData(final RemoteData data)
                throws IOException {
            try {
                return downloadDataFromBackend(data);
            } catch (IOException | DataRetrievalException exception) {
                if (log.isWarnEnabled()) {
                    log.warn("Could not connect to data source. [exception=({})]",
                            exception.getMessage(), exception);
                }

                throw new IOException("Could not connect to data source.", exception);
            }
        }

        private InputStream downloadDataFromBackend(final RemoteData data) throws IOException {
            InputStream backendData;
            if (data.getAccessUrl().toString().startsWith(getRoutesApiUrl())) {
                backendData = getData(retriever, data.getAccessUrl());
            } else {
                if (!data.getAuthentication().isEmpty()) {
                    backendData = getData(httpSvc, data.getAccessUrl(), data.getAuthentication());
                } else {
                    backendData = getData(httpSvc, data.getAccessUrl());
                }
            }

            return backendData;
        }

        private InputStream getData(final DataRetrievalService service, final URL target)
                throws IOException, DataRetrievalException {
            return service.get(target, queryInput).getData();
        }

        private InputStream getData(final DataRetrievalService service, final URL target,
                                    final List<? extends HttpAuthentication> authentications)
                throws IOException, DataRetrievalException {
            return service.get(target, queryInput, authentications).getData();
        }
    }

    /**
     * Dispatches data via Camel routes.
     */
    @RequiredArgsConstructor
    private class DataDispatcher {

        /**
         * The IDs of the routes to use for dispatching the data.
         */
        private final List<URI> routeIds;

        /**
         * The data.
         */
        private final @NonNull InputStream dataStream;

        /**
         * Dispatches the data via all specified routes.
         *
         * @return the data.
         * @throws IOException if the data cannot be read or there is a failure in one of the
         *                     routes.
         */
        public InputStream dispatch() throws IOException {
            if (routeIds != null && !routeIds.isEmpty()) {
                try {
                    final var data = dataStream.readAllBytes();
                    dataStream.close();
                    for (var routeId: routeIds) {
                        routeDispatcher.send(routeId, data);
                    }
                    return new ByteArrayInputStream(data);
                } catch (IOException | DataDispatchException exception) {
                    if (log.isWarnEnabled()) {
                        log.warn("Could not send data via route. [exception=({})]",
                                exception.getMessage(), exception);
                    }

                    throw new IOException("Could not send data via route.", exception);
                }
            } else {
                return dataStream;
            }
        }
    }
}
