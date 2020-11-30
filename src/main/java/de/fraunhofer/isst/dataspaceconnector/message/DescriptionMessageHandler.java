package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ConnectorConfigurationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;

import static de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.DescriptionMessageHandler} handles all
 * incoming messages that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as
 * part one in the multipart message. This header must have the correct '@type' reference as
 * defined in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} JsonTypeName
 * annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a
 * proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(DescriptionRequestMessageImpl.class)
public class DescriptionMessageHandler implements MessageHandler<DescriptionRequestMessageImpl> {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionMessageHandler.class);

    private final OfferedResourceService offeredResourceService;
    private final TokenProvider tokenProvider;
    private final ConfigurationContainer configurationContainer;
    private final SerializerProvider serializerProvider;

    @Autowired
    /**
     * <p>Constructor for DescriptionMessageHandler.</p>
     *
     * @param offeredResourceService a
     * {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param provider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider}
     *                object.
     * @param connector a {@link de.fraunhofer.iais.eis.Connector} object.
     * @param configProducer a
     * {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a
     * {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     *
     * @throws IllegalArgumentException - if one of the parameters is null.
     */
    public DescriptionMessageHandler(@NotNull OfferedResourceService offeredResourceService,
                                     @NotNull TokenProvider tokenProvider,
                                     @NotNull ConfigurationContainer configurationContainer,
                                     @NotNull SerializerProvider serializerProvider) throws IllegalArgumentException {
        if (offeredResourceService == null)
            throw new IllegalArgumentException("The OfferedResourceService cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (serializerProvider == null)
            throw new IllegalArgumentException("The SerializerProvider cannot be null.");


        this.offeredResourceService = offeredResourceService;
        this.tokenProvider = tokenProvider;
        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This message implements the logic that is needed to handle the message. As it just returns
     * the input as string the messagePayload-InputStream is converted to a String.
     *
     * @throws RuntimeException - if the response body failed to be build or requestMessage is null.
     */
    @Override
    public MessageResponse handleMessage(DescriptionRequestMessageImpl requestMessage,
                                         MessagePayload messagePayload) throws RuntimeException {
        if (requestMessage == null) {
            LOGGER.error("Cannot respond when there is no request.");
            throw new IllegalArgumentException("The requestMessage cannot be null.");
        }

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
                return constructConnectorSelfDescription(requestMessage.getId(),
                        requestMessage.getIssuerConnector());
            } catch (RuntimeException exception) {
                // Something went wrong (e.g invalid connector config), try to fix it at a higher
                // level
                throw new RuntimeException("Failed to construct a connector resource catalog " +
                        "description.",
                        exception);
            }
        }
    }

    /**
     * Construct the response message for a given resource description request message.
     *
     * @param requestMessage The message containing the resource request.
     * @return The response message to the passed request.
     * @throws ConnectorConfigurationException - if the connector is not configurated.
     * @throws RuntimeException                - if the response message could not be constructed.
     */
    private MessageResponse constructResourceDescription(DescriptionRequestMessageImpl requestMessage) throws RuntimeException {
        try {
            // Get a local copy of the connector for read access
            final var connector = getConnector();

            try {
                // Create the response header
                final var responseMessageHeader = new DescriptionResponseMessageBuilder()
                        ._securityToken_(tokenProvider.getTokenJWS())
                        ._correlationMessage_(requestMessage.getId())
                        ._issued_(getGregorianNow())
                        ._issuerConnector_(connector.getId())
                        ._modelVersion_(connector.getOutboundModelVersion())
                        ._senderAgent_(connector.getId())
                        ._recipientConnector_(Util.asList(requestMessage.getIssuerConnector()))
                        .build();

                Assert.notNull(responseMessageHeader, "The responseMessageHeader object cannot be null");

                // Find the requested resource
                final var resourceId = UUIDUtils.uuidFromUri(requestMessage.getRequestedElement());
                final var resource = offeredResourceService.getOfferedResources().get(resourceId);

                if (resource == null) {
                    // The resource has been found, send the description.
                    return BodyResponse.create(responseMessageHeader, resource.toRdf());
                } else {
                    // The resource has not been found, inform and reject.
                    LOGGER.info(String.format("Resource %s requested by %s could not be found.",
                            resourceId, requestMessage.getId()));

                    return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.format(
                            "The resource %s could not be found.", resourceId), connector.getId()
                            , connector.getOutboundModelVersion());
                }
            } catch (UUIDFormatException exception) {
                // No resource uuid could be found in the request, reject the message.
                LOGGER.info(String.format("Description requested by %s has no valid uuid: %s.",
                        requestMessage.getId(),
                        requestMessage.getRequestedElement()));

                return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS,
                        "No valid resource id found.",
                        connector.getId(),
                        connector.getOutboundModelVersion());
            } catch (ConstraintViolationException exception) {
                // The response could not be constructed.
                throw new RuntimeException("Failed to construct the response message.", exception);
            }
        } catch (ConnectorConfigurationException exception) {
            // The connector must be set.
            throw exception;
        }
    }

    /**
     * Construct a resource catalog description message for the connector.
     *
     * @param requestId       The id of the message requesting resource information.
     * @param issuerConnector The id of the conenctor requsting the resource information.
     * @return A response message containing the resource catalog of the connector.
     * @throws ConnectorConfigurationException - if the connector is not configurated.
     * @throws RuntimeException                - if the response message could not be constructed or the
     *                                         connector could not be serialized.
     */
    private MessageResponse constructConnectorSelfDescription(URI requestId, URI issuerConnector) throws RuntimeException {
        Assert.notNull(serializerProvider, "The SerializerProvider should not be null.");
        Assert.notNull(offeredResourceService, "The OfferedResourceService should not be null.");
        Assert.notNull(tokenProvider, "The TokenProvider should not be null.");

        try {
            // Create a connector with a list of currently offered resources
            var connector = (BaseConnectorImpl) getConnector();
            connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                    ._offeredResource_(offeredResourceService.getResourceList())
                    .build()));


            // Create the response header
            final var responseMessageHeader = new DescriptionResponseMessageBuilder()
                    ._securityToken_(tokenProvider.getTokenJWS())
                    ._correlationMessage_(requestId)
                    ._issued_(getGregorianNow())
                    ._issuerConnector_(connector.getId())
                    ._modelVersion_(connector.getOutboundModelVersion())
                    ._senderAgent_(connector.getId())
                    ._recipientConnector_(Util.asList(issuerConnector))
                    .build();

            Assert.notNull(responseMessageHeader, "The responseMessageHeader object cannot be null");

            // Answer with the resource description
            return BodyResponse.create(responseMessageHeader, serializerProvider.getSerializer().serialize(connector));
        } catch (ConnectorConfigurationException exception) {
            // The connector must be set.
            throw exception;
        } catch (ConstraintViolationException exception) {
            // The response could not be constructed.
            throw new RuntimeException("Failed to construct the response message.", exception);
        } catch (IOException exception) {
            // The connector could not be serialized.
            throw new RuntimeException("Failed to serialize the connector.", exception);
        }
    }

    private Connector getConnector() throws ConnectorConfigurationException {
        Assert.notNull(configurationContainer, "The config cannot be null.");

        final var connector = configurationContainer.getConnector();
        if (connector == null) {
            // The connector is needed for every answer and cannot be null
            throw new ConnectorConfigurationException("No connector configurated.");
        }

        return connector;
    }
}
