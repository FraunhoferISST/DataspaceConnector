package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.RequestMessage;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * The service for request messages.
 */
@Service
public class RequestMessageService extends MessageService {
    /**
     * The configuration container.
     */
    @Autowired
    private ConfigurationContainer configurationContainer;

    /**
     * The dat provider.
     */
    @Autowired
    private DapsTokenProvider tokenProvider;

    /**
     * Build an ids artifact request message.
     *
     * @param recipient The recipient of the request.
     * @param artifactId The id of the artifact.
     * @param contractId The id of the contract.
     */
    public RequestMessage buildArtifactRequestMessage(final URI recipient, final URI artifactId, final URI contractId) throws MessageBuilderException {
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
     * Build an ids description request message.
     *
     * @param recipient The recipient of the request.
     * @param resourceId The id of the resource.
     * @return The ids request message.
     * @throws MessageBuilderException If the message could not be built.
     */
    public RequestMessage buildDescriptionRequestMessage(final URI recipient, final URI resourceId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

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

    public RequestMessage buildContractRequestMessage(final URI recipient, final URI contractId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();
    }

    public Map<String, String> sendArtifactRequest(final URI recipient, final URI artifactId, final URI contractId, final String payload) throws MessageException {
        final var header = buildArtifactRequestMessage(recipient, artifactId, contractId);
        return sendMessage(header, payload, recipient);
    }

    public Map<String, String> sendDescriptionRequest(final URI recipient, final URI resourceId, final String payload) throws MessageException {
        final var header = buildDescriptionRequestMessage(recipient, resourceId);
        return sendMessage(header, payload, recipient);
    }

    public Map<String, String> sendContractRequest(final URI recipient, final URI contractId, final String payload) throws MessageException {
        final var header = buildContractRequestMessage(recipient, contractId);
        return sendMessage(header, payload, recipient);
    }
}
