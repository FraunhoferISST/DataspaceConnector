package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.BaseConnectorImpl;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.ResourceCatalogBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.response.DescriptionResponseMessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.utils.UUIDUtils;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This @{@link DescriptionMessageHandler} handles all
 * incoming messages that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessage} as
 * part one in the multipart message. This header must have the correct '@type' reference as defined
 * in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessage} JsonTypeName annotation.
 */
@Component
@SupportedMessageType(DescriptionRequestMessage.class)
public class DescriptionMessageHandler implements MessageHandler<DescriptionRequestMessage> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionMessageHandler.class);

    private final DescriptionResponseMessageService descriptionResponseMessageService;
    private final ResourceService resourceService;
    private final Connector connector;

    /**
     * Constructor for DescriptionMessageHandler.
     *
     * @throws IllegalArgumentException - if one of the parameters is null.
     */
    @Autowired
    public DescriptionMessageHandler(IdsUtils idsUtils,
        DescriptionResponseMessageService descriptionResponseMessageService,
        OfferedResourceServiceImpl offeredResourceService) throws IllegalArgumentException {
        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        if (descriptionResponseMessageService == null)
            throw new IllegalArgumentException("The DescriptionResponseMessageService cannot be null.");

        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceServiceImpl cannot be null.");

        this.descriptionResponseMessageService = descriptionResponseMessageService;
        this.resourceService = offeredResourceService;
        this.connector = idsUtils.getConnector();
    }

    /**
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @throws RuntimeException - if the response body failed to be build or requestMessage is
     *                          null.
     */
    @Override
    public MessageResponse handleMessage(DescriptionRequestMessage requestMessage,
        MessagePayload messagePayload) throws RuntimeException {
        if (requestMessage == null) {
            LOGGER.warn("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

        if (!descriptionResponseMessageService.versionSupported(requestMessage.getModelVersion())) {
            LOGGER.warn("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(
                RejectionReason.VERSION_NOT_SUPPORTED,
                "Information model version not supported.",
                connector.getId(), connector.getOutboundModelVersion());
        }

        descriptionResponseMessageService.setParameter(requestMessage.getIssuerConnector(),
            requestMessage.getId());

        // Check if a concrete resource has been requested
        if (requestMessage.getRequestedElement() != null) {
            // A specific resource has been requested
            try {
                return constructResourceDescription(requestMessage);
            } catch (RuntimeException exception) {
                // Something went wrong (e.g invalid connector config), try to fix it at a higher
                // level
                throw new RuntimeException("Failed to construct a resource description.",
                    exception);
            }
        } else {
            // No resource has been requested, return a resource catalog
            try {
                return constructConnectorSelfDescription(requestMessage);
            } catch (RuntimeException exception) {
                // Something went wrong (e.g invalid connector config), try to fix it at a higher
                // level
                throw new RuntimeException("Failed to construct a self-description.",
                    exception);
            }
        }
    }

    /**
     * Constructs the response message for a given resource description request message.
     *
     * @param requestMessage The message containing the resource request.
     * @return The response message to the passed request.
     * @throws ConnectorConfigurationException - if the connector is not configurated.
     * @throws RuntimeException                - if the response message could not be constructed.
     */
    public MessageResponse constructResourceDescription(DescriptionRequestMessage requestMessage)
        throws RuntimeException {
        try {
            // Find the requested resource
            final var resourceId = UUIDUtils.uuidFromUri(requestMessage.getRequestedElement());
            final var resource = ((OfferedResourceServiceImpl) resourceService).getOfferedResources().get(resourceId);

            if (resource != null) {
                // The resource has been found, send the description.
                return BodyResponse.create(descriptionResponseMessageService.buildHeader(), resource.toRdf());
            } else {
                // The resource has not been found, inform and reject.
                LOGGER.debug("Resource could not be found. [id=({}), resourceId=({})]",
                    resourceId, requestMessage.getId());

                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                    "The resource %s could not be found.", resourceId), connector.getId()
                    , connector.getOutboundModelVersion());
            }
        } catch (UUIDFormatException exception) {
            // No resource uuid could be found in the request, reject the message.
            LOGGER.debug(
                "Description has no valid uuid. [id=({}), requestedElement=({}), exception=({})].",
                requestMessage.getId(), requestMessage.getRequestedElement(),
                exception.getMessage());

            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                "No valid resource id found.",
                connector.getId(),
                connector.getOutboundModelVersion());
        } catch (ConstraintViolationException | MessageBuilderException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Constructs a resource catalog description message for the connector.
     *
     * @return A response message containing the resource catalog of the connector.
     * @throws ConnectorConfigurationException - if the connector is not configurated.
     * @throws RuntimeException                - if the response message could not be constructed or
     *                                         the connector could not be serialized.
     */
    public MessageResponse constructConnectorSelfDescription(
        DescriptionRequestMessage requestMessage) throws RuntimeException {
        try {
            // Create a connector with a list of currently offered resources
            var connector = (BaseConnectorImpl) this.connector;
            connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                ._offeredResource_(new ArrayList<>(resourceService.getResources()))
                .build()));

            // Answer with the resource description
            return BodyResponse.create(descriptionResponseMessageService.buildHeader(),
                connector.toRdf());
        } catch (ConstraintViolationException | MessageBuilderException exception) {
            // The response could not be constructed.
            return ErrorResponse.withDefaultHeader(
                RejectionReason.INTERNAL_RECIPIENT_ERROR,
                "Response could not be constructed.",
                connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
