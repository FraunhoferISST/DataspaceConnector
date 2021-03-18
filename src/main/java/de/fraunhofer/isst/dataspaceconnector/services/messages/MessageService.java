package de.fraunhofer.isst.dataspaceconnector.services.messages;

import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnexpectedMessageType;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.utils.IdsUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageService {

    /**
     * Service for description request messages.
     */
    private final @NonNull DescriptionRequestService descriptionRequestService;

    /**
     * Service for description request messages.
     */
    private final @NonNull ContractRequestService contractRequestService;

    /**
     * Build and send a description request message. Validate response.
     *
     * @param recipient The recipient.
     * @param elementId The requested element.
     * @return The response map.
     * @throws MessageException      If message handling failed.
     * @throws UnexpectedMessageType If the validation failed.
     */
    public Map<String, String> sendDescriptionRequestMessage(final URI recipient,
                                                             final URI elementId)
            throws MessageException, UnexpectedMessageType {
        final var desc = new DescriptionRequestMessageDesc(elementId);
        desc.setRecipient(recipient);

        return descriptionRequestService.sendMessage(desc, "");
    }

    /**
     * Build and send a description request message. Validate response.
     *
     * @param recipient       The recipient.
     * @param contractRequest The contract request.
     * @return The response map.
     * @throws MessageException      If message handling failed.
     * @throws UnexpectedMessageType If the validation failed.
     */
    public Map<String, String> sendContractRequestMessage(final URI recipient,
                                                          final ContractRequest contractRequest)
            throws MessageException, UnexpectedMessageType, ConstraintViolationException {
        final var contractId = contractRequest.getId();
        final var contractRdf = IdsUtils.getContractRequestAsRdf(contractRequest);

        final var desc = new ContractRequestMessageDesc(contractId);
        desc.setRecipient(recipient);

        return contractRequestService.sendMessage(desc, contractRdf);
    }
}
