package io.dataspaceconnector.services.messages.handler;

import io.dataspaceconnector.services.messages.MessageResponseService;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import de.fraunhofer.iais.eis.Message;

@Log4j2
@RequiredArgsConstructor
@Component("VersionValidator")
public class VersionValidator extends IdsValidator {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    /**
     * Service for building and sending message responses.
     */
    private final @NonNull MessageResponseService responseService;

    @Override
    protected void processInternal(final Request<? extends Message, ?> request) throws Exception {
        messageService.validateIncomingMessage(request.getHeader());
        log.info("Validating and stuff!");
    }
}
