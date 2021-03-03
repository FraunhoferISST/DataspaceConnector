package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import de.fraunhofer.iais.eis.ArtifactResponseMessageBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessageBuilder;
import de.fraunhofer.iais.eis.ResponseMessage;
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
public class ResponseMessageService extends MessageService {
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
     * Build ids artifact response message.
     *
     * @param recipient The recipient of the response.
     * @param contractId The id of the contract.
     * @param correlationId The id of the request.
     * @return The ids response message.
     * @throws MessageBuilderException If the message could not be built.
     */
    public ResponseMessage buildArtifactResponseMessage(final URI recipient, final URI contractId, final URI correlationId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ArtifactResponseMessageBuilder()
            ._securityToken_(tokenProvider.getDAT())
            ._correlationMessage_(correlationId)
            ._issued_(getGregorianNow())
            ._issuerConnector_(connector.getId())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._senderAgent_(connector.getId())
            ._recipientConnector_(Util.asList(recipient))
            ._transferContract_(contractId)
            .build();
    }

    /**
     * Build ids description response message.
     *
     * @param recipient The message's recipient.
     * @param correlationId The correlation message id.
     * @return The ids response message.
     * @throws MessageBuilderException If the message could not be built.
     */
    public ResponseMessage buildDescriptionResponseMessage(final URI recipient, final URI correlationId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new DescriptionResponseMessageBuilder()
                ._securityToken_(tokenProvider.getDAT())
                ._correlationMessage_(correlationId)
                ._issued_(getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    public ResponseMessage buildContractAgreementMessage(final URI recipient, final URI correlationId) throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractAgreementMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._securityToken_(tokenProvider.getDAT())
                ._recipientConnector_(Util.asList(recipient))
                ._correlationMessage_(correlationId)
                .build();
    }

    public Map<String, String> sendContractAgreement(final URI recipient, final URI correlationId, final String payload) throws MessageException {
        final var header = buildContractAgreementMessage(recipient, correlationId);
        return sendMessage(header, payload, recipient);
    }
}
