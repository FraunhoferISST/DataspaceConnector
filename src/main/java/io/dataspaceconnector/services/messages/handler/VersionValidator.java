package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.services.messages.types.DescriptionResponseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component("VersionValidator")
public class VersionValidator extends IdsValidator<RouteMsg<? extends Message, ?>> {

    /**
     * Service for handling response messages.
     */
    private final @NonNull DescriptionResponseService messageService;

    @Override
    protected void processInternal(RouteMsg<? extends Message, ?> msg) throws Exception {
        messageService.validateIncomingMessage(msg.getHeader());
        log.info("Validating and stuff!");
    }
}
