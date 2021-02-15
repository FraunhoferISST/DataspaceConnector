package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for description messages
 */
@Service
public class DescriptionMessageService extends MessageService {

    private final Logger LOGGER = LoggerFactory.getLogger(DescriptionMessageService.class);

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private URI recipient, resourceId, correlationMessageId;

    /**
     * Constructor
     *
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param configurationContainer The container with the configuration
     * @param serializerProvider The service for serializing
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public DescriptionMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        ConfigurationContainer configurationContainer, SerializerProvider serializerProvider) throws IllegalArgumentException {
        super(idsHttpService, serializerProvider);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RequestMessage buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        if (resourceId == null) {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        } else {
            return new DescriptionRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedElement_(resourceId)
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                .build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildResponseHeader() throws MessageException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new DescriptionResponseMessageBuilder()
            ._securityToken_(tokenProvider.getDAT())
            ._correlationMessage_(correlationMessageId)
            ._issued_(getGregorianNow())
            ._issuerConnector_(connector.getId())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._senderAgent_(connector.getId())
            ._recipientConnector_(Util.asList(recipient))
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getRecipient() {
        return recipient;
    }

    /**
     * Set the request parameters for the message
     *
     * @param recipient The recipient of the request
     * @param resourceId The id of the resource
     */
    public void setRequestParameters(URI recipient, URI resourceId) {
        this.recipient = recipient;
        this.resourceId = resourceId;
    }

    /**
     * Set the response parameters for the artifact message
     *
     * @param recipient The recipient of the response
     * @param correlationMessageId The id of the correlation message
     */
    public void setResponseParameters(URI recipient, URI correlationMessageId) {
        this.recipient = recipient;
        this.correlationMessageId = correlationMessageId;
    }
}
