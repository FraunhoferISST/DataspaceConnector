package de.fraunhofer.isst.dataspaceconnector.services.messages.implementation;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractMessageService extends MessageService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private URI recipient, contractId, correlationMessage;

    @Autowired
    public ContractMessageService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        OfferedResourceServiceImpl resourceService, ConfigurationContainer configurationContainer,
        IdsUtils idsUtils, SerializerProvider serializerProvider) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Message buildRequestHeader() throws MessageBuilderException {
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

    @Override
    public Message buildResponseHeader() throws MessageException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractAgreementMessageBuilder()
            ._issued_(getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getDAT())
            ._recipientConnector_(Util.asList(recipient))
            ._correlationMessage_(correlationMessage)
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setRequestParameters(URI recipient, URI contractId) {
        this.recipient = recipient;
        this.contractId = contractId;
    }

    public void setResponseParameters(URI recipient, URI correlationMessage, URI contractId) {
        this.recipient = recipient;
        this.correlationMessage = correlationMessage;
        this.contractId = contractId;
    }

    public ContractRequest buildContractRequest(Contract contract) {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractRequestBuilder()
            ._consumer_(connector.getMaintainer())
            ._provider_(contract.getProvider())
            ._contractDate_(getGregorianNow())
            ._contractStart_(getGregorianNow())
            ._obligation_(contract.getObligation())
            ._permission_(contract.getPermission())
            ._prohibition_(contract.getProhibition())
            ._provider_(contract.getProvider())
            .build();
    }

    /**
     * Build contract agreement. Keeps parameters and id.
     */
    public ContractAgreement buildContractAgreement(Contract contract) {
        if (contractId == null) {
            return new ContractAgreementBuilder()
                ._consumer_(contract.getConsumer())
                ._provider_(contract.getProvider())
                ._contractDate_(contract.getContractDate())
                ._contractStart_(contract.getContractStart())
                ._obligation_(contract.getObligation())
                ._permission_(contract.getPermission())
                ._prohibition_(contract.getProhibition())
                ._provider_(contract.getProvider())
                .build();
        } else {
            return new ContractAgreementBuilder(contractId)
                ._consumer_(contract.getConsumer())
                ._provider_(contract.getProvider())
                ._contractDate_(contract.getContractDate())
                ._contractStart_(contract.getContractStart())
                ._obligation_(contract.getObligation())
                ._permission_(contract.getPermission())
                ._prohibition_(contract.getProhibition())
                ._provider_(contract.getProvider())
                .build();
        }
    }
}
