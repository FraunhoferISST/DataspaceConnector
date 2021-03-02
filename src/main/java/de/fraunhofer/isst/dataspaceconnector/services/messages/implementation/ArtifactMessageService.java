package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.resource.ResourceException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RequestedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ResourceService;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.UUID;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for artifact messages
 */
@Service
public class ArtifactMessageService extends MessageService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private final ResourceService resourceService;
    private URI recipient, artifactId, contractId, correlationMessageId;

    /**
     * Constructor
     *
     * @param configurationContainer The container with the configuration
     * @param tokenProvider The service for providing tokens
     * @param idsHttpService The service for ids messaging
     * @param resourceService The service for resources
     * @param requestedResourceService The service for requested resources
     * @param serializerProvider The service for serializing
     * @throws IllegalArgumentException if any of the parameters is null
     */
    @Autowired
    public ArtifactMessageService(ConfigurationContainer configurationContainer,
        DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        OfferedResourceServiceImpl resourceService, SerializerProvider serializerProvider,
        RequestedResourceServiceImpl requestedResourceService) throws IllegalArgumentException {
        super(idsHttpService, serializerProvider, resourceService, configurationContainer);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (requestedResourceService == null)
            throw new IllegalArgumentException("The ResourceService cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
        this.resourceService = requestedResourceService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildRequestHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ArtifactRequestMessageBuilder()
            ._issued_(getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._requestedArtifact_(artifactId)
            ._securityToken_(tokenProvider.getDAT())
            ._recipientConnector_(Util.asList(recipient))
            ._transferContract_(contractId)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Message buildResponseHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ArtifactResponseMessageBuilder()
            ._securityToken_(tokenProvider.getDAT())
            ._correlationMessage_(correlationMessageId)
            ._issued_(getGregorianNow())
            ._issuerConnector_(connector.getId())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._senderAgent_(connector.getId())
            ._recipientConnector_(Util.asList(recipient))
            ._transferContract_(contractId)
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
     * Set the request parameters for the artifact message
     *
     * @param recipient The recipient of the request
     * @param artifactId The id of the artifact
     * @param contractId The id of the contract
     */
    public void setRequestParameters(URI recipient, URI artifactId, URI contractId) {
        this.recipient = recipient;
        this.artifactId = artifactId;
        this.contractId = contractId;
    }

    /**
     * Set the response parameters for the artifact message
     *
     * @param recipient The recipient of the response
     * @param contractId The id of the contract
     * @param correlationMessageId The id of the request
     */
    public void setResponseParameters(URI recipient, URI contractId, URI correlationMessageId) {
        this.recipient = recipient;
        this.contractId = contractId;
        this.correlationMessageId = correlationMessageId;
    }

    /**
     * Saves the data string to the internal database.
     *
     * @param response   The data resource as string.
     * @param resourceId The resource uuid.
     * @throws ResourceException if any.
     */
    public void saveData(String response, UUID resourceId) throws ResourceException {
        try {
            resourceService.addData(resourceId, response);
        } catch (Exception e) {
            throw new ResourceException("Data could not be saved. " + e.getMessage());
        }
    }
}
