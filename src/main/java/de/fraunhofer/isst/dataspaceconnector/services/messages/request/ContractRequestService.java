package de.fraunhofer.isst.dataspaceconnector.services.messages.request;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.RequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util;
import de.fraunhofer.isst.ids.framework.spring.starter.IDSHttpService;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import java.net.URI;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractRequestService extends RequestService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractRequestService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private final IdsUtils idsUtils;
    private URI recipient, contractId;

    @Autowired
    public ContractRequestService(TokenProvider tokenProvider, IDSHttpService idsHttpService,
        IdsUtils idsUtils, OfferedResourceServiceImpl resourceService)
        throws IllegalArgumentException {
        super(idsHttpService, resourceService);

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        this.connector = idsUtils.getConnector();
        this.tokenProvider = tokenProvider;
        this.idsUtils = idsUtils;
    }

    @Override
    public Message buildHeader() throws MessageBuilderException {
        return new ContractRequestMessageBuilder()
            ._issued_(Util.getGregorianNow())
            ._modelVersion_(connector.getOutboundModelVersion())
            ._issuerConnector_(connector.getId())
            ._senderAgent_(connector.getId())
            ._securityToken_(tokenProvider.getTokenJWS())
            ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(recipient))
            ._transferContract_(contractId)
            .build();
    }

    @Override
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient, URI contractId) {
        this.recipient = recipient;
        this.contractId = contractId;
    }

    public ContractRequest buildContractRequest(Contract contract) {
        return new ContractRequestBuilder()
            ._consumer_(connector.getMaintainer())
            ._provider_(contract.getProvider())
            ._contractDate_(idsUtils.getGregorianOf(new Date()))
            ._contractStart_(idsUtils.getGregorianOf(new Date()))
            ._obligation_(contract.getObligation())
            ._permission_(contract.getPermission())
            ._prohibition_(contract.getProhibition())
            ._provider_(contract.getProvider())
            .build();
    }
}
