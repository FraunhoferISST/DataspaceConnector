package io.dataspaceconnector.service.message.handler.validator;

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.type.DescriptionResponseService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Validates any incoming message by checking whether the message is empty and whether it references
 * an Infomodel version supported by this connector.
 */
@RequiredArgsConstructor
@Component("MessageHeaderValidator")
class MessageHeaderValidator extends IdsValidator<Request<? extends Message, ?,
        Optional<Jws<Claims>>>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Checks whether the message is empty and whether it references an Infomodel version supported
     * by this connector.
     *
     * @param msg the incoming message.
     * @throws Exception if the message is empty or references an unsupported Infomodel version.
     */
    @Override
    protected void processInternal(final Request<? extends Message, ?, Optional<Jws<Claims>>> msg)
            throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
    }
}
