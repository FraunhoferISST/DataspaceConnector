package de.fraunhofer.isst.dataspaceconnector.message;

import static de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactRequestMessageImpl;
import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.InvalidResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RequestFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resource.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.ArtifactMessageHandler} handles all
 * incoming messages that have a {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part
 * one in the multipart message. This header must have the correct '@type' reference as defined in
 * the {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} JsonTypeName annotation. In this
 * example, the received payload is not defined and will be returned immediately. Usually, the
 * payload would be well defined as well, such that it can be deserialized into a proper
 * Java-Object.
 */
@Component
@SupportedMessageType(ArtifactRequestMessageImpl.class)
public class ArtifactMessageHandler implements MessageHandler<ArtifactRequestMessageImpl> {

    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactMessageHandler.class);

    private final TokenProvider provider;
    private final ResourceService resourceService;
    private final PolicyHandler policyHandler;
    private final IdsUtils idsUtils;

    /**
     * Constructor for ArtifactMessageHandler.
     *
     * @throws IllegalArgumentException if one of the passed parameters is null
     */
    @Autowired
    public ArtifactMessageHandler(@NotNull OfferedResourceServiceImpl offeredResourceService,
        @NotNull TokenProvider tokenProvider,
        @NotNull PolicyHandler policyHandler,
        @NotNull IdsUtils idsUtils) throws IllegalArgumentException {
        if (offeredResourceService == null) {
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");
        }

        if (tokenProvider == null) {
            throw new IllegalArgumentException("The TokenProvider cannot be null.");
        }

        if (policyHandler == null) {
            throw new IllegalArgumentException("The PolicyHandler cannot be null.");
        }

        if (idsUtils == null) {
            throw new IllegalArgumentException("The IdsUtils cannot be null.");
        }

        this.resourceService = offeredResourceService;
        this.provider = tokenProvider;
        this.policyHandler = policyHandler;
        this.idsUtils = idsUtils;
    }

    /**
     * This message implements the logic that is needed to handle the message. As it returns the
     * input as string the messagePayload-InputStream is converted to a String.
     *
     * @throws ConnectorConfigurationException - if no connector is configurated.
     * @throws RuntimeException                - if the response body failed to be build.
     */
    @Override
    // NOTE: Why ArtifactRequestMessageImpl (focus on the Impl) part. It defeats the point of having
    //       the interface.
    // NOTE: This function is really hard to read
    // NOTE: Make runtime exception more concrete and add ConnectorConfigurationException, ResourceTypeException
    public MessageResponse handleMessage(ArtifactRequestMessageImpl requestMessage,
        MessagePayload messagePayload) throws RuntimeException {
        Assert.notNull(provider, "The TokenProvider cannot be null.");
        Assert.notNull(idsUtils, "The IdsUtils cannot be null.");
        Assert.notNull(resourceService, "The OfferedResourceService cannot be null.");
        Assert.notNull(policyHandler, "The PolicyHandler cannot be null.");

        try {
            // Get a local copy of the connector for read access
            final var connector = idsUtils.getConnector();

            try {
                // Extract the artifact id
                final var artifactId = extractArtifactIdFromRequest(requestMessage);
                Assert.notNull(artifactId, "The artifactId cannot be null.");

                // Find the requested resource
                final var requestedResource = findResourceFromArtifactId(artifactId);
                if (requestedResource == null) {
                    // The resource was not found, reject and inform the requester.
                    LOGGER.debug("Resource could not be found. [id=({}), artifactId=({})]",
                        artifactId, requestMessage.getId());

                    return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                        "An artifact with the given uuid is not known to the "
                            + "connector.",
                        connector.getId(), connector.getOutboundModelVersion());
                }

                try {
                    // Get the resource metadata
                    final var resourceId = UUIDUtils.uuidFromUri(requestedResource.getId());
                    Assert.notNull(resourceId, "The resourceId should be filled.");

                    // Check the access policy
                    final var resourceMetadata = resourceService.getMetadata(resourceId);
                    var canProvisionData = false;
                    try {
                        canProvisionData = policyHandler
                            .onDataProvision(resourceMetadata.getPolicy());
                    } catch (IOException exception) {
                        // The provisioning failed, reject the request
                        LOGGER.warn(
                            "Failed to provision data. [id=({}), resourceId=({}), exception=({})]",
                            resourceId, requestMessage.getId(), exception.getMessage());

                        return ErrorResponse
                            .withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                                "The data could not be provisioned.",
                                connector.getId(), connector.getOutboundModelVersion());
                    }

                    try {
                        if (canProvisionData) {
                            // Get the data from source
                            String data = null;
                            try {
                                data = resourceService
                                    .getDataByRepresentation(resourceId, artifactId);
                            } catch (ResourceNotFoundException exception) {
                                LOGGER.debug("Representation could not be found. "
                                        + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                    requestMessage.getId(), resourceId, artifactId,
                                    exception.getMessage());
                                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                                    "Resource not found.", connector.getId(),
                                    connector.getOutboundModelVersion());
                            } catch (InvalidResourceException exception) {
                                LOGGER.debug("Representation is not in a valid format. "
                                        + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                    requestMessage.getId(), resourceId, artifactId,
                                    exception.getMessage());
                                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                                    "Resource not found.", connector.getId(),
                                    connector.getOutboundModelVersion());
                            } catch (ResourceException exception) {
                                LOGGER.warn("Representation could not be received. "
                                        + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                                    requestMessage.getId(), resourceId, artifactId,
                                    exception.getMessage());
                                return ErrorResponse
                                    .withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                                        "Something went wrong.", connector.getId(),
                                        connector.getOutboundModelVersion());
                            }

                            Assert.notNull(data, "The data string should not be empty.");

                            // Build and send the response
                            final var responseMessage = new ArtifactResponseMessageBuilder()
                                ._securityToken_(provider.getTokenJWS())
                                ._correlationMessage_(requestMessage.getId())
                                ._issued_(getGregorianNow())
                                ._issuerConnector_(connector.getId())
                                ._modelVersion_(connector.getOutboundModelVersion())
                                ._senderAgent_(connector.getId())
                                ._recipientConnector_(
                                    Util.asList(requestMessage.getIssuerConnector()))
                                .build();

                            Assert.notNull(responseMessage,
                                "The responseMessage object cannot be null");

                            return BodyResponse.create(responseMessage, data);
                        } else {
                            // The conditions for reading this resource have not been met.
                            LOGGER.debug("Request policy restriction detected for request."
                                    + "[id=({}), pattern=({})]",
                                requestMessage.getId(),
                                policyHandler.getPattern(resourceMetadata.getPolicy()));

                            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED,
                                "Policy restriction detected: You are not authorized to receive this data.",
                                connector.getId(),
                                connector.getOutboundModelVersion());
                        }
                    } catch (IOException exception) {
                        // The could not be loaded from source.
                        LOGGER.warn("Could not receive data from source ."
                                + "[id=({}), resourceId=({}), artifactId=({}), exception=({})]",
                            requestMessage.getId(), resourceId, artifactId, exception.getMessage());

                        return ErrorResponse
                            .withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR,
                                "The data could not be read from source.", connector.getId(),
                                connector.getOutboundModelVersion());
                    } catch (ConstraintViolationException exception) {
                        // The response could not be constructed.
                        throw new RuntimeException("Failed to construct the response message.",
                            exception);
                    } catch (ResourceNotFoundException exception) {
                        // The resource could be not be found.
                        LOGGER.debug(
                            "The resource could not be found. [id=({}), resourceId=({}), exception=({})]",
                            requestMessage.getId(), resourceId, exception.getMessage());
                        return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND,
                            "Resource not found.", connector.getId(),
                            connector.getOutboundModelVersion());
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
            } catch (UUIDFormatException exception) {
                // No resource uuid could be found in the request, reject the message.
                LOGGER.debug(
                    "Resource has no valid uuid. [id=({}), artifactUri=({}), exception=({})]",
                    requestMessage.getId(), requestMessage.getRequestedArtifact(),
                    exception.getMessage());

                return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                    "No valid resource id found.",
                    connector.getId(),
                    connector.getOutboundModelVersion());
            }
        } catch (ConnectorConfigurationException exception) {
            // The connector must be set.
            throw exception;
        }
    }

    private Resource findResourceFromArtifactId(UUID artifactId) {
        Assert.notNull(resourceService, "The resource service cannot be null.");

        for (final var resource : resourceService.getResources()) {
            for (final var representation : resource.getRepresentation()) {
                final var representationId = UUIDUtils.uuidFromUri(representation.getId());

                if (representationId.equals(artifactId)) {
                    return resource;
                }
            }
        }

        return null;
    }

    private UUID extractArtifactIdFromRequest(ArtifactRequestMessage requestMessage)
        throws RequestFormatException {
        try {
            return UUIDUtils.uuidFromUri(requestMessage.getRequestedArtifact());
        } catch (UUIDFormatException exception) {
            throw new RequestFormatException(
                "The uuid could not extracted from request" + requestMessage.getId(),
                exception);
        }
    }
}
