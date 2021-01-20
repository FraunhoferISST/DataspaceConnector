package de.fraunhofer.isst.dataspaceconnector.services.messages.request;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.RequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.OfferedResourceServiceImpl;
import de.fraunhofer.isst.ids.framework.communication.http.IDSHttpService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.daps.DapsTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
public class ContractRequestService extends RequestService {

    private final ConfigurationContainer configurationContainer;
    private final DapsTokenProvider tokenProvider;
    private URI recipient, contractId;

    @Autowired
    public ContractRequestService(DapsTokenProvider tokenProvider, IDSHttpService idsHttpService,
        OfferedResourceServiceImpl resourceService, ConfigurationContainer configurationContainer)
        throws IllegalArgumentException {
        super(idsHttpService, resourceService);

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
    public URI getRecipient() {
        return recipient;
    }

    public void setParameter(URI recipient, URI contractId) {
        this.recipient = recipient;
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
}
