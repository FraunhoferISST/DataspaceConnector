package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import io.dataspaceconnector.exceptions.ContractException;
import io.dataspaceconnector.exceptions.UnconfirmedAgreementException;
import io.dataspaceconnector.model.messages.MessageProcessedNotificationMessageDesc;
import io.dataspaceconnector.services.EntityResolver;
import io.dataspaceconnector.services.EntityUpdateService;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.services.messages.types.MessageProcessedNotificationService;
import io.dataspaceconnector.services.usagecontrol.PolicyExecutionService;
import io.dataspaceconnector.utils.ContractUtils;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("AgreementComparisonProcessor")
@RequiredArgsConstructor
public class AgreementComparisonProcessor extends IdsProcessor<RouteMsg<ContractAgreementMessageImpl, ContractAgreement>> {

    /**
     * Service for resolving entities.
     */
    private final @NonNull EntityResolver entityResolver;

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    /**
     * Service for updating database entities from ids object.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Policy execution point.
     */
    private final @NonNull PolicyExecutionService executionService;

    /**
     * Service for handling notification messages.
     */
    private final @NonNull MessageProcessedNotificationService messageService;

    @Override
    protected Response processInternal(RouteMsg<ContractAgreementMessageImpl, ContractAgreement> msg) throws Exception {
        final var agreement = msg.getBody();
        final var storedAgreement = entityResolver.getAgreementByUri(agreement.getId());
        final var storedIdsAgreement = deserializationService
                .getContractAgreement(storedAgreement.getValue());

        if (!ContractUtils.compareContractAgreements(agreement, storedIdsAgreement)) {
            throw new ContractException("Received agreement does not match stored agreement.");
        }

        if (!updateService.confirmAgreement(storedAgreement)) {
            throw new UnconfirmedAgreementException(storedAgreement, "Could not confirm agreement.");
        }

        //TODO move to own processor?
        executionService.sendAgreement(agreement);

        final var issuer = MessageUtils.extractIssuerConnector(msg.getHeader());
        final var messageId = MessageUtils.extractMessageId(msg.getHeader());

        final var desc = new MessageProcessedNotificationMessageDesc(issuer, messageId);
        final var responseHeader = messageService.buildMessage(desc);

        return new Response(responseHeader, "Received contract agreement message.");
    }

}
