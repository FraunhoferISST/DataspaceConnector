package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.config.PolicyConfiguration;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractAgreementNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.contract.ContractException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.services.EntityDependencyResolver;
import de.fraunhofer.isst.dataspaceconnector.services.messages.implementation.ResponseMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v1.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.RuleService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.dataspaceconnector.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.MessageResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * This @{@link ArtifactRequestHandler} handles all
 * incoming messages that have a {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part
 * one in the multipart message. This header must have the correct '@type' reference as defined in
 * the {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(ArtifactRequestMessageImpl.class)
@RequiredArgsConstructor
public class ArtifactRequestHandler implements MessageHandler<ArtifactRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactRequestHandler.class);

    private final @NonNull PolicyHandler policyHandler;
    private final @NonNull ResponseMessageService messageService;
    private final @NonNull ContractAgreementService contractAgreementService;
    private final @NonNull ConfigurationContainer configurationContainer;
    private final @NonNull ObjectMapper objectMapper;
    private final @NonNull PolicyConfiguration policyConfiguration;

    private final @NonNull EntityDependencyResolver entityDependencyResolver;
    private final @NonNull ArtifactService artifactBFFService;
    private final @NonNull ResourceService<OfferedResource, ?> resourceService;
    private final @NonNull ContractService contractService;
    private final @NonNull RuleService ruleService;

    /**
     * This message implements the logic that is needed to handle the message. As it returns the
     * input as string the messagePayload-InputStream is converted to a String.
     *
     * @param requestMessage The request message
     * @param messagePayload The message payload
     * @return The response message
     * @throws RuntimeException if the response body failed to be build.
     */
    @Override
    // NOTE: Make runtime exception more concrete and add ConnectorConfigurationException, ResourceTypeException
    public MessageResponse handleMessage(final ArtifactRequestMessageImpl requestMessage, final MessagePayload messagePayload) throws RuntimeException {
        if (requestMessage == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        // Check if version is supported.
        if (!messageService.isVersionSupported(requestMessage.getModelVersion())) {
            LOGGER.debug("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                RejectionReason.VERSION_NOT_SUPPORTED,
                "Information model version not supported.",
                connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            // Find artifact and matching resource.
            final var artifactId = extractArtifactIdFromRequest(requestMessage);
            final var resource = entityDependencyResolver.findResourceFromArtifactId(artifactId);

            if (resource.isPresent()) {
                // The resource was not found, reject and inform the requester.
                LOGGER.debug("Resource could not be found. [id=({}), artifactId=({})]",
                    requestMessage.getId(), artifactId);

                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                    "An artifact with the given uuid is not known to the "
                        + "connector.",
                    connector.getId(), connector.getOutboundModelVersion());
            }

            // Check if the transferred contract matches the requested artifact.
            if (!checkTransferContract(requestMessage.getTransferContract(),
                requestMessage.getRequestedArtifact())) {
                LOGGER.debug("Contract agreement could not be found. [id=({}), contractId=({})]",
                    requestMessage.getId(), requestMessage.getTransferContract());

                return ErrorResponse.withDefaultHeader(
                    RejectionReason.BAD_PARAMETERS,
                    "Missing transfer contract or wrong contract.",
                    connector.getId(), connector.getOutboundModelVersion());
            }

            try {
                // Find the requested resource and its metadata.
                final var contracts = resource.get().getContracts().values();
                final var rules = contractService.get((UUID) contracts.toArray()[0]).getRules().values();
                // TODO Should this happen in the backend?
                final var policy = ruleService.get((UUID)rules.toArray()[0]).getValue();

                try {
                    // Check if the policy allows data access. TODO: Change to contract agreement. (later)
                    if (policyHandler.onDataProvision(policy)) {
                        Object data;
                        try {
                            // Read query parameters from message payload.
                            QueryInput queryInputData = objectMapper.readValue(IOUtils.toString(
                                    messagePayload.getUnderlyingInputStream(),
                                    StandardCharsets.UTF_8), QueryInput.class);
                            // Get the data from source.
                            data = artifactBFFService.getData(artifactId, queryInputData);
                        } catch (ResourceNotFoundException exception) {
                            LOGGER.debug("Resource could not be found. "
                                    + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                requestMessage.getId(), resource.get().getId(), artifactId,
                                exception.getMessage());
                            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                                "Resource not found.", connector.getId(),
                                connector.getOutboundModelVersion());
                        } catch (InvalidResourceException exception) {
                            LOGGER.debug("Resource is not in a valid format. "
                                    + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                requestMessage.getId(), resource.get().getId(), artifactId,
                                exception.getMessage());
                            return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                                "Something went wrong.", connector.getId(),
                                connector.getOutboundModelVersion());
                        } catch (ResourceException exception) {
                            LOGGER.warn("Resource could not be received. "
                                    + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                requestMessage.getId(), resource.get().getId(), artifactId,
                                exception.getMessage());
                            return ErrorResponse
                                .withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                                    "Something went wrong.", connector.getId(),
                                    connector.getOutboundModelVersion());
                        } catch (IOException exception) {
                            LOGGER.debug("Message payload could not be read. [id=({}), " +
                                            "resourceId=({}), artifactId=({}), exception=({})]",
                                    requestMessage.getId(), resource.get().getId(), artifactId,
                                    exception.getMessage());
                            return ErrorResponse
                                    .withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                                            "Malformed payload.", connector.getId(),
                                            connector.getOutboundModelVersion());
                        }

                        // Build artifact response.
                        final var header = messageService.buildArtifactResponseMessage(
                                requestMessage.getIssuerConnector(),
                                requestMessage.getTransferContract(),
                                requestMessage.getId()
                        );
                        return BodyResponse.create(header, data);
                    } else {
                        // The conditions for reading this resource have not been met.
                        LOGGER.debug("Request policy restriction detected for request."
                                + "[id=({}), pattern=({})]",
                            requestMessage.getId(),
                            policyHandler.getPattern(policy));
                        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                            "Policy restriction detected: You are not authorized to receive this data.",
                            connector.getId(),
                            connector.getOutboundModelVersion());
                    }
                } catch (ConstraintViolationException | MessageException exception) {
                    // The response could not be constructed.
                    throw new RuntimeException("Failed to construct the response message.",
                        exception);
                } catch (IllegalArgumentException exception) {
                    LOGGER.warn("Could not deserialize contract. "
                            + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                        requestMessage.getId(), resource.get().getId(), artifactId, exception.getMessage());
                    return ErrorResponse.withDefaultHeader(
                        RejectionReason.INTERNAL_RECIPIENT_ERROR,
                        "Policy check failed.",
                        connector.getId(), connector.getOutboundModelVersion());
                }
            } catch (UUIDFormatException exception) {
                // The resource from the database is not identified via uuids.
                LOGGER.debug(
                    "The resource is not valid. The uuid is not valid. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                    "Resource not found.", connector.getId(),
                    connector.getOutboundModelVersion());
            } catch (ResourceNotFoundException exception) {
                // The resource could be not be found.
                LOGGER.debug("The resource could not be found. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                    "Resource not found.", connector.getId(),
                    connector.getOutboundModelVersion());
            } catch (InvalidResourceException exception) {
                // The resource could be not be found.
                LOGGER.debug("The resource is not valid. [id=({}), exception=({})]",
                    requestMessage.getId(), exception.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                    "Resource not found.", connector.getId(),
                    connector.getOutboundModelVersion());
            }
        } catch (UUIDFormatException | RequestFormatException exception) {
            // No resource uuid could be found in the request, reject the message.
            LOGGER.debug(
                "Artifact has no valid uuid. [id=({}), artifactUri=({}), exception=({})]",
                requestMessage.getId(), requestMessage.getRequestedArtifact(),
                exception.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "No valid resource id found.",
                connector.getId(),
                connector.getOutboundModelVersion());
        } catch (ContractAgreementNotFoundException exception) {
            LOGGER.warn("Could not load contract from database. "
                    + "[id=({}), contractId=({}),exception=({})]",
                requestMessage.getId(), requestMessage.getTransferContract(), exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                RejectionReason.BAD_PARAMETERS,
                "Invalid transfer contract id.",
                connector.getId(), connector.getOutboundModelVersion());
        } catch (ContractException exception) {
            LOGGER.warn("Could not deserialize contract. "
                    + "[id=({}), contractId=({}),exception=({})]",
                requestMessage.getId(), requestMessage.getTransferContract(), exception.getMessage());
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Something went wrong.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Extract the artifact id.
     *
     * @param requestMessage The artifact request message
     * @return The artifact id
     * @throws RequestFormatException if uuid could not be extracted.
     */
    private UUID extractArtifactIdFromRequest(final ArtifactRequestMessage requestMessage)
        throws RequestFormatException {
        try {
            // TODO This extraction should be unnecessary. The artifact uri should enough for mapping to endpoint id
            return UUIDUtils.uuidFromUri(requestMessage.getRequestedArtifact());
        } catch (UUIDFormatException exception) {
            throw new RequestFormatException(
                "The uuid could not extracted from request" + requestMessage.getId(),
                exception);
        }
    }

    /**
     * Check if the transfer contract is not null and valid.
     *
     * @param contractId The id of the contract
     * @param artifactId The id of the artifact
     * @return True if everything's fine.
     */
    private boolean checkTransferContract(URI contractId, URI artifactId) throws ContractException {
        final var policyNegotiation = policyConfiguration.isPolicyNegotiation();

        if (policyNegotiation) {
            if (contractId == null) {
                return false;
            } else {
                String contractToString;
                try {
                    UUID uuid = UUIDUtils.uuidFromUri(contractId);
                    // Get contract agreement from database.
                    ResourceContract contract = contractAgreementService.getContract(uuid);
                    // Get contract from database entry.
                    contractToString = contract.getContract();
                } catch (Exception e) {
                    throw new ContractAgreementNotFoundException("Contract could not be loaded "
                        + "from database.");
                }

                Contract agreement;
                try {
                    agreement = policyHandler.validateContract(contractToString);
                } catch (RequestFormatException exception) {
                    throw new ContractException("Could not deserialize contract.");
                }

                URI extractedId = entityDependencyResolver.getArtifactIdFromContract(agreement);
                return extractedId.equals(artifactId);
            }
        } else {
            return true;
        }
    }
}
