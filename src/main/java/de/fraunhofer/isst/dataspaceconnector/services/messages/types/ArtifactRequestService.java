package de.fraunhofer.isst.dataspaceconnector.services.messages.types;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ArtifactResponseMessageImpl;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageResponseException;
import de.fraunhofer.isst.dataspaceconnector.model.QueryInput;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import de.fraunhofer.isst.dataspaceconnector.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Map;

import static de.fraunhofer.isst.ids.framework.util.IDSUtils.getGregorianNow;

/**
 * Message service for ids artifact request messages.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public final class ArtifactRequestService
        extends AbstractMessageService<ArtifactRequestMessageDesc> {

    /**
     * @throws IllegalArgumentException If desc is null.
     */
    @Override
    public Message buildMessage(final ArtifactRequestMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessages.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        final var recipient = desc.getRecipient();
        final var artifactId = desc.getRequestedArtifact();
        final var contractId = desc.getTransferContract();

        return new ArtifactRequestMessageBuilder()
                ._issued_(getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._requestedArtifact_(artifactId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                ._transferContract_(contractId)
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return ArtifactResponseMessageImpl.class;
    }

    /**
     * Build and send an artifact request message.
     *
     * @param recipient   The recipient.
     * @param elementId   The requested artifact.
     * @param agreementId The transfer contract.
     * @return The response map.
     * @throws MessageException If message handling failed.
     */
    public Map<String, String> sendMessage(final URI recipient, final URI elementId,
                                           final URI agreementId) throws MessageException {
        return sendMessage(recipient, elementId, agreementId, null);
    }

    /**
     * Send artifact request message.
     *
     * @param recipient   The recipient.
     * @param elementId   The requested artifact.
     * @param agreementId The transfer contract.
     * @param queryInput  The query input.
     * @return The response map.
     * @throws MessageException If message handling failed.
     */
    public Map<String, String> sendMessage(
            final URI recipient, final URI elementId, final URI agreementId,
            final QueryInput queryInput) throws MessageException {
        String payload = "";
        if (queryInput != null) {
            try {
                payload = new ObjectMapper().writeValueAsString(queryInput);
            } catch (JsonProcessingException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Failed to parse query. Loading everything. [exception=({})]",
                            e.getMessage(), e);
                }
            }
        }

        return send(new ArtifactRequestMessageDesc(recipient, elementId, agreementId), payload);
    }

    /**
     * Check if the response message is of type artifact response.
     *
     * @param response The response as map.
     * @return True if the response type is as expected.
     * @throws MessageResponseException if the response could not be read.
     */
    public boolean validateResponse(final Map<String, String> response) throws MessageResponseException {
        return isValidResponseType(response);
    }
}
