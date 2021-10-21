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

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.NotImplemented;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.common.exception.UnreachableLineException;
import io.dataspaceconnector.common.net.HttpService;
import io.dataspaceconnector.common.net.QueryInput;
import io.dataspaceconnector.common.net.RetrievalInformation;
import io.dataspaceconnector.common.usagecontrol.AccessVerificationInput;
import io.dataspaceconnector.common.usagecontrol.PolicyVerifier;
import io.dataspaceconnector.common.usagecontrol.VerificationResult;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.artifact.Artifact;
import io.dataspaceconnector.model.artifact.ArtifactDesc;
import io.dataspaceconnector.model.artifact.ArtifactFactory;
import io.dataspaceconnector.model.artifact.ArtifactImpl;
import io.dataspaceconnector.model.artifact.LocalData;
import io.dataspaceconnector.model.artifact.RemoteData;
import io.dataspaceconnector.model.base.AbstractFactory;
import io.dataspaceconnector.repository.ArtifactRepository;
import io.dataspaceconnector.repository.AuthenticationRepository;
import io.dataspaceconnector.repository.BaseEntityRepository;
import io.dataspaceconnector.repository.DataRepository;
import io.dataspaceconnector.service.ArtifactRetriever;
import io.dataspaceconnector.service.resource.base.BaseEntityService;
import io.dataspaceconnector.service.resource.base.RemoteResolver;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * Constructor for ArtifactService.
     *
     * @param repository               The artifact repository.
     * @param factory                  The artifact logic repository.
     * @param dataRepository           The data repository.
     * @param httpService              The HTTP service for fetching remote data.
     * @param authenticationRepository The AuthType repository.
     */
    public ArtifactService(final BaseEntityRepository<Artifact> repository,
                           final AbstractFactory<Artifact, ArtifactDesc> factory,
                           final @NonNull DataRepository dataRepository,
                           final @NonNull HttpService httpService,
                           final @NonNull AuthenticationRepository authenticationRepository) {
        super(repository, factory);
        this.dataRepo = dataRepository;
        this.httpSvc = httpService;
        this.authRepo = authenticationRepository;
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
            }
        }

        return super.persist(tmp);
    }

    /**
     * Get the artifacts data. If agreements for this resource exist, all of them will be tried for
     * data access.
     *
     * @param accessVerifier Checks if the data access should be allowed.
     * @param retriever      Retrieves the data from an external source.
     * @param artifactId     The id of the artifact.
     * @param queryInput     The query for the backend.
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
                               final QueryInput queryInput)
            throws PolicyRestrictionException, IOException {
        final var agreements =
                ((ArtifactRepository) getRepository()).findRemoteOriginAgreements(artifactId);
        if (agreements.size() > 0) {
            return tryToAccessDataByUsingAnyAgreement(accessVerifier, retriever, artifactId,
                    queryInput, agreements);
        }

        // The artifact is not assigned to any requested resources. It must be offered if it exists.
        return getDataFromInternalDB((ArtifactImpl) get(artifactId), queryInput);
    }

    private InputStream tryToAccessDataByUsingAnyAgreement(
            final PolicyVerifier<AccessVerificationInput> accessVerifier,
            final ArtifactRetriever retriever, final UUID artifactId, final QueryInput queryInput,
            final List<URI> agreements) throws IOException {
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
                return getData(accessVerifier, retriever, artifactId, info);
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
                               final RetrievalInformation information)
            throws PolicyRestrictionException, IOException {
        // Check the artifact exists and access is granted.
        final var artifact = get(artifactId);
        verifyDataAccess(accessVerifier,
                new AccessVerificationInput(information.getTransferContract(), artifact));

        // Make sure the data exists and is up to date.
        if (shouldDownload(artifact, information)) {
            final var data = downloadAndUpdateData(retriever, artifactId, information, artifact);
            incrementAccessCounter(artifact);
            return data;
        }

        // Artifact exists, access granted, data exists and data up to date.
        return getDataFromInternalDB((ArtifactImpl) artifact, null);
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
                                              final Artifact artifact)
            throws IOException {
        final var dataStream = retriever.retrieve(artifactId,
                artifact.getRemoteAddress(),
                information.getTransferContract(),
                information.getQueryInput());
        return setData(artifactId, dataStream);
    }

    /**
     * Get the data from the internal database. No policy enforcement is performed here!
     *
     * @param artifact   The artifact which data should be returned.
     * @param queryInput The query for the data backend. May be null.
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
            throw new UnreachableLineException("Unknown data type.");
        }

        incrementAccessCounter(artifact);

        return rawData;
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
     * Get local data.
     *
     * @param data The data container.
     * @return The stored data.
     */
    private InputStream getData(final LocalData data) {
        return toInputStream(data.getValue());
    }

    /**
     * Get remote data.
     *
     * @param data       The data container.
     * @param queryInput The query for the backend.
     * @return The stored data.
     * @throws IOException if IO errors occur.
     */
    private InputStream getData(final RemoteData data, final QueryInput queryInput)
            throws IOException {
        try {
            return downloadDataFromBackend(data, queryInput);
        } catch (IOException exception) {
            if (log.isWarnEnabled()) {
                log.warn("Could not connect to data source. [exception=({})]",
                        exception.getMessage(), exception);
            }

            throw new IOException("Could not connect to data source.", exception);
        }
    }

    private InputStream downloadDataFromBackend(final RemoteData data,
                                                final QueryInput queryInput) throws IOException {
        InputStream backendData;
        if (!data.getAuthentication().isEmpty()) {
            backendData = httpSvc.get(data.getAccessUrl(), queryInput,
                    data.getAuthentication())
                    .getBody();
        } else {
            backendData = httpSvc.get(data.getAccessUrl(), queryInput).getBody();
        }
        return backendData;
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

    private InputStream toInputStream(final byte[] data) {
        if (data == null) {
            return ByteArrayInputStream.nullInputStream();
        } else {
            return new ByteArrayInputStream(data);

        }
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
}
