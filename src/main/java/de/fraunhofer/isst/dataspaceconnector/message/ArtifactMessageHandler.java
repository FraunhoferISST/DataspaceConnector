package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UUIDFormatException;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.UUIDUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.eclipse.rdf4j.util.UUIDable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.ArtifactMessageHandler} handles all incoming messages that
 * have a {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part one in the multipart message. This header
 * must have the correct '@type' reference as defined in the {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(ArtifactRequestMessageImpl.class)
public class ArtifactMessageHandler implements MessageHandler<ArtifactRequestMessageImpl> {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactMessageHandler.class);

    private final TokenProvider provider;
    private final Connector connector;

    // NOTE: Why are they not final?
    private OfferedResourceService offeredResourceService;
    private PolicyHandler policyHandler;

    @Autowired
    /**
     * <p>Constructor for ArtifactMessageHandler.</p>
     *
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param provider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param connector a {@link de.fraunhofer.iais.eis.Connector} object.
     * @param policyHandler a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler} object.
     */
    public ArtifactMessageHandler(OfferedResourceService offeredResourceService, TokenProvider provider,
                                  ConfigurationContainer configurationContainer, PolicyHandler policyHandler) {
        this.offeredResourceService = offeredResourceService;
        this.provider = provider;
        // NOTE: Make sure the configurationContainer is not null
        this.connector = configurationContainer.getConnector();
        this.policyHandler = policyHandler;

        // NOTE: Can all the parameters be null as valid argument?
    }

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    // NOTE: Why ArtifactRequestMessageImpl (focus on the Impl) part. It defeats the point of having the interface.
    public MessageResponse handleMessage(ArtifactRequestMessageImpl requestMessage, MessagePayload messagePayload) {
        Assert.notNull(provider, "The provider object cannot be null.");
        Assert.notNull(connector, "The connector object cannot be null.");
        Assert.notNull(offeredResourceService, "The offeredResourceService object cannot be null.");
        Assert.notNull(policyHandler, "The offeredResourceService object cannot be null.");

        // NOTE: Handle de.fraunhofer.iais.eis.util.ConstraintViolationException
        // NOTE: Is provider.getTokenJWS() == null allowed?
        // NOTE: Is requestMessage.getId() == null allowed?
        // NOTE: Why is this constructed here and not when the resource is needed? At least
        //       after the resource has been found? It disrupts the logic.
        final ResponseMessage responseMessage = new ArtifactResponseMessageBuilder()
                ._securityToken_(provider.getTokenJWS())
                ._correlationMessage_(requestMessage.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(requestMessage.getIssuerConnector()))
                .build();

        Assert.notNull(responseMessage, "The responseMessage object cannot be null");

        // NOTE: Handle the exception hidden in uuidFromUri
        UUID artifactId = UUIDUtils.uuidFromUri(requestMessage.getRequestedArtifact());
        URI requestedResource = null;

        // NOTE: Why is this a loop and not a hash map?
        for (Resource resource : offeredResourceService.getResourceList()) {
            for (Representation representation : resource.getRepresentation()) {
                UUID representationId = UUIDUtils.uuidFromUri(representation.getId());

                if (representationId.equals(artifactId)) {
                    requestedResource = resource.getId();
                    // NOTE: Can the loop be canceled? The id is unique and there should be no
                    //       second object overriding the requestedResource
                }
            }
        }

        // NOTE: requestedResource allows null values. The usage of exception suggest that a
        //       situation occurred that hinders the function from performing its intended purpose.


        try {
            // NOTE: Handle hidden exception in uuidFromUri
            UUID resourceId = UUIDUtils.uuidFromUri(requestedResource);
            ResourceMetadata resourceMetadata = offeredResourceService.getMetadata(resourceId);
            try {
                // NOTE: Handle the hidden IOException in onDataProvision. The exception description is vague.
                if (policyHandler.onDataProvision(resourceMetadata.getPolicy())) {
                    // NOTE: Handle hidden generic Exception. The exception description is vague.
                    String data = offeredResourceService.getDataByRepresentation(resourceId, artifactId);
                    return BodyResponse.create(responseMessage, data);
                } else {
                    // This is not an error! Its within the scope of the function.
                    LOGGER.error("Policy restriction detected: " + policyHandler.getPattern(resourceMetadata.getPolicy()));
                    return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED, "Policy restriction detected: You are not authorized to receive this data.", connector.getId(), connector.getOutboundModelVersion());
                }
            } catch (Exception e) {
                // NOTE: Get rid of the generic exception.
                LOGGER.error("Exception: {}", e.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR, e.getMessage(), connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (Exception e) {
            // NOTE: Get rid of the generic exception.
            LOGGER.error("Resource could not be found.");
            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, "An artifact with the given uuid is not known to the connector: {}" + e.getMessage(), connector.getId(), connector.getOutboundModelVersion());
        }
    }
}