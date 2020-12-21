package de.fraunhofer.isst.dataspaceconnector.services.messages.request;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.Contract;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.iais.eis.ContractRequestMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.isst.dataspaceconnector.exceptions.message.MessageBuilderException;
import de.fraunhofer.isst.dataspaceconnector.services.messages.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.utils.IdsUtils;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
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
public class ContractAgreementMessageService extends MessageService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementMessageService.class);

    private final Connector connector;
    private final TokenProvider tokenProvider;
    private final IdsUtils idsUtils;
    private URI recipient, contractId;

    @Autowired
    public ContractAgreementMessageService(ConfigurationContainer configurationContainer,
        TokenProvider tokenProvider, IDSHttpService idsHttpService, IdsUtils idsUtils) {
        super(idsHttpService);

        if (configurationContainer == null)
            throw new IllegalArgumentException("The ConfigurationContainer cannot be null.");

        if (tokenProvider == null)
            throw new IllegalArgumentException("The TokenProvider cannot be null.");

        if (idsUtils == null)
            throw new IllegalArgumentException("The IdsUtils cannot be null.");

        this.connector = configurationContainer.getConnector();
        this.tokenProvider = tokenProvider;
        this.idsUtils = idsUtils;
    }

    @Override
    public Message buildHeader() throws MessageBuilderException {
        return new ContractAgreementMessageBuilder()
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

    public ContractAgreement buildContractAgreement(Contract contract) {
        return new ContractAgreementBuilder()
            ._consumer_(contract.getConsumer())
            ._provider_(contract.getProvider())
            ._contractDate_(idsUtils.getGregorianOf(new Date()))
            ._obligation_(contract.getObligation())
            ._permission_(contract.getPermission())
            ._prohibition_(contract.getProhibition())
            ._provider_(contract.getProvider())
            .build();
    }
}
