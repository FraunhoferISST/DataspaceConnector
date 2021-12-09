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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import io.dataspaceconnector.common.exception.DataDispatchException;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.routing.dataretrieval.RetrievalInformation;
import io.dataspaceconnector.common.routing.RouteDataDispatcher;
import io.dataspaceconnector.common.usagecontrol.AccessVerificationInput;
import io.dataspaceconnector.common.usagecontrol.PolicyVerifier;
import io.dataspaceconnector.common.usagecontrol.VerificationResult;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.Data;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.ArtifactRetriever;
import io.dataspaceconnector.service.DataRetriever;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.base.RemoteResolver;
import io.dataspaceconnector.service.resource.relation.ArtifactRouteService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.SerializationUtils;

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
     * Repository for storing AuthTypes.
     */
    private final @NonNull AuthenticationRepository authRepo;

    /**
     * Service for managing the relation between artifacts and routes.
     */
    private final @NonNull ArtifactRouteService artifactRouteSvc;

    /**
     * Retrieves data from the local database, remote HTTP services and Camel routes.
     */
    private final @NonNull DataRetriever dataRetriever;

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
     * @param authenticationRepository The AuthType repository.
     * @param artifactRouteService     The Artifact-Route-relation service.
     * @param retriever                The data retriever.
     * @param routeDataDispatcher      The route data dispatcher.
     */
    public ArtifactService(final BaseEntityRepository<Artifact> repository,
                           final AbstractFactory<Artifact, ArtifactDesc> factory,
                           final @NonNull DataRepository dataRepository,
                           final @NonNull AuthenticationRepository authenticationRepository,
                           final @NonNull ArtifactRouteService artifactRouteService,
                           final @NonNull DataRetriever retriever,
                           final @NonNull RouteDataDispatcher routeDataDispatcher) {
        super(repository, factory);
        this.dataRepo = dataRepository;
        this.authRepo = authenticationRepository;
        this.artifactRouteSvc = artifactRouteService;
        this.dataRetriever = retriever;
        this.routeDispatcher = routeDataDispatcher;
    }

    /**
     * Creates a new artifact and the corresponding data. If it references a route, the route link
     * is also created and the route is deployed in Camel.
     *
     * @param desc The description of the artifact.
     * @return The persisted artifact
     * @throws InvalidEntityException if the input is invalid or the referenced Camel route cannot
     *                                be created.
     */
    @Override
    public Artifact create(final ArtifactDesc desc) {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var artifact = getFactory().create(desc);
        final var tmp = (ArtifactImpl) artifact;

        if (tmp.getData() != null) {
            // The data element is new, insert
            if (tmp.getData() instanceof RemoteData) {
                var data = (RemoteData) tmp.getData();
                data.getAuthentication().forEach(authRepo::saveAndFlush);
            }
            final var persistedData = dataRepo.saveAndFlush(tmp.getData());

            if (tmp.getData() instanceof LocalData) {
                final var factory = (ArtifactFactory) getFactory();
                factory.updateByteSize(artifact, ((LocalData) tmp.getData()).getValue());
            } else if (tmp.getData() instanceof RemoteData) {
                final var url = ((RemoteData) tmp.getData()).getAccessUrl();
                artifactRouteSvc.ensureSingleArtifactPerRoute(url, artifact.getId());
                artifactRouteSvc.checkForValidRoute(url);
                final var persisted = persist(artifact);
                try {
                    artifactRouteSvc.createRouteLink(url, persisted);
                    return persisted;
                } catch (InvalidEntityException exception) {
                    // Artifact and data should not be persisted if route cannot be created.
                    getRepository().deleteById(persisted.getId());
                    dataRepo.deleteRemoteData(persistedData.getId());
                    throw exception;
                }
            }
        }

        return persist(artifact);
    }

    /**
     * Updates and artifact and its data.
     *
     * @param artifactId The artifact ID.
     * @param desc The description of the artifact.
     * @return The updated artifact.
     * @throws InvalidEntityException if the input is invalid or the referenced Camel route cannot
     *                                be created.
     */
    @Override
    public Artifact update(final UUID artifactId, final ArtifactDesc desc) {
        Utils.requireNonNull(artifactId, ErrorMessage.ENTITYID_NULL);
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        var artifact = get(artifactId);
        final var cached = SerializationUtils.clone(artifact);

        if (getFactory().update(artifact, desc)) {
            final var tmp = (ArtifactImpl) artifact;
            final var tmpData = tmp.getData();
            Data persistedData = null;
            Data storedCopy = null;
            if (tmpData.getId() == null) {
                // The data element is new, insert
                if (tmpData instanceof RemoteData) {
                    var data = (RemoteData) tmpData;
                    data.getAuthentication().forEach(authRepo::saveAndFlush);
                }
                persistedData = dataRepo.saveAndFlush(tmp.getData());
            } else {
                // The data element exists already, check if an update is required
                storedCopy = dataRepo.getById(tmp.getData().getId());
                if (!storedCopy.equals(tmp.getData())) {
                    persistedData = dataRepo.saveAndFlush(tmp.getData());
                }
            }

            if (tmp.getData() instanceof LocalData) {
                final var factory = (ArtifactFactory) getFactory();
                factory.updateByteSize(artifact, ((LocalData) tmp.getData()).getValue());
                artifact = persist(artifact);
            } else if (tmp.getData() instanceof RemoteData) {
                final var url = ((RemoteData) tmp.getData()).getAccessUrl();
                artifactRouteSvc.ensureSingleArtifactPerRoute(url, artifact.getId());
                artifactRouteSvc.checkForValidRoute(url);
                try {
                    artifactRouteSvc.createRouteLink(url, artifact);
                    artifact = persist(artifact);
                } catch (InvalidEntityException exception) {
                    // If the route cannot be deployed, revert changes to artifact and data
                    persist(cached);
                    if (storedCopy != null) {
                        dataRepo.saveAndFlush(storedCopy);
                    } else {
                        dataRepo.deleteRemoteData(persistedData.getId());
                    }

                    throw exception;
                }
            }
        }

        return artifact;
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
        final var artifact = get(artifactId);
        var data = dataRetriever.retrieveData((ArtifactImpl) artifact, queryInput);
        return returnData(artifact, data, routeIds);
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
     * Increases the access counter before returning data. If a list of route IDs for dispatching
     * the data is specified, the data is dispatched via all referenced routes before returning it.
     *
     * @param artifact The artifact.
     * @param data     The data.
     * @param routeIds The route IDs for dispatching data.
     * @return The data.
     * @throws IOException if the data cannot be read or there is a failure in one of the
     *                     routes.
     */
    private InputStream returnData(final Artifact artifact, final InputStream data,
                                   final List<URI> routeIds) throws IOException {
        incrementAccessCounter(artifact);
        return new DataDispatcher(routeIds, data).dispatch();
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
        var data = dataRetriever.retrieveData((ArtifactImpl) artifact,
                information.getQueryInput());
        return returnData(artifact, data, routeIds);
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

        final var artifact = (ArtifactImpl) get(artifactId);
        artifactRouteSvc.removeRouteLink(artifact);

        getRepository().deleteById(artifactId);
    }

    /**
     * Returns the route associated with an artifact, if any.
     *
     * @param artifactId The artifact ID.
     * @return the associated route, if any. Null otherwise.
     * @throws io.dataspaceconnector.common.exception.ResourceNotFoundException if the referenced
     *                                                                          route is unknown.
     */
    public Route getAssociatedRoute(final UUID artifactId) {
        final var artifact = (ArtifactImpl) get(artifactId);
        return artifactRouteSvc.getAssociatedRoute(artifact);
    }

    /**
     * Gets the deleted status of the artifacts data.
     *
     * @param artifactId The artifact uuid.
     * @return True if artifact data null, else false.
     */
    public boolean isDataDeleted(final UUID artifactId) {
        final var artifact = get(artifactId);
        final var currentData = ((ArtifactImpl) artifact).getData();
        if (currentData instanceof LocalData) {
            final var value = ((LocalData) currentData).getValue();
            return (value == null || !(value.length > 0));
        } else {
            // Only local data deletion supported.
            return false;
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
