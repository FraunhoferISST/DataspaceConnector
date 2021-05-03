package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.RdfBuilderException;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids contract agreement messages.
 */
@Service
public final class ContractAgreementService
        extends AbstractMessageService<ContractAgreementMessageDesc> {

    /**
     * @throws IllegalArgumentException if desc is null.
     */
    @Override
    public Message buildMessage(final ContractAgreementMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();
        final var correlationMessage = desc.getCorrelationMessage();

        return new ContractAgreementMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._correlationMessage_(correlationMessage)
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return MessageProcessedNotificationMessageImpl.class;
    }

    /**
     * Build and send a contract agreement message.
     *
     * @param recipient The recipient.
     * @param agreement The contract agreement.
     * @return The response map.
     * @throws MessageException         if message handling failed.
     * @throws RdfBuilderException      if the contract agreement rdf string could not be built.
     * @throws IllegalArgumentException if contract agreement is null.
     */
    public Map<String, String> sendMessage(final URI recipient, final ContractAgreement agreement)
            throws MessageException, ConstraintViolationException {
        Utils.requireNonNull(agreement, ErrorMessages.ENTITY_NULL);

        final var contractRdf = IdsUtils.toRdf(agreement);
        return send(new ContractAgreementMessageDesc(recipient, agreement.getId()), contractRdf);
    }

    /**
     * Check if the response message is of type message processed notification.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException if the response could not be read.
     */
    public boolean validateResponse(final Map<String, String> response) throws MessageResponseException {
        return isValidResponseType(response);
    }
}
