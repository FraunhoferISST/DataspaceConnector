package de.fraunhofer.isst.dataspaceconnector.services.messages.response;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.ResponseService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class ContractResponseService extends ResponseService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractResponseService.class);

    private final ConfigurationContainer configurationContainer;
    private final TokenProvider tokenProvider;
    private URI recipient, correlationMessage, contractId;

    @Autowired
    public ContractResponseService(TokenProvider tokenProvider, IDSHttpService idsHttpService,
        IdsUtils idsUtils, SerializerProvider serializerProvider,
        OfferedResourceServiceImpl resourceService,
        ConfigurationContainer configurationContainer) throws IllegalArgumentException {
        super(idsHttpService, idsUtils, serializerProvider, resourceService);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        this.configurationContainer = configurationContainer;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Message buildHeader() throws MessageBuilderException {
        // Get a local copy of the current connector.
        var connector = configurationContainer.getConnector();

        return new ContractAgreementMessageBuilder()
            ._issued_(Util.getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getTokenJWS())
            ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
            ._correlationMessage_(correlationMessage)
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient, URI correlationMessage, URI contractId) {
        this.recipient = recipient;
        this.correlationMessage = correlationMessage;
        this.contractId = contractId;
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
