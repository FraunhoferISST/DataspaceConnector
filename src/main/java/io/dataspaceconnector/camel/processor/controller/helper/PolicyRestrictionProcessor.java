package io.dataspaceconnector.camel.processor.controller.helper;

import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.util.ErrorMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Processes an InvalidResponseException occurred during an artifact request to throw a
 * PolicyRestrictionException.
 */
@Component("PolicyRestrictionProcessor")
@Log4j2
public
class PolicyRestrictionProcessor extends IdsHelperProcessor {

    /**
     * Throws a PolicyRestrictionException, if the exception that occurred in the route is an
     * InvalidResponseException.
     * @param exchange the exchange.
     * @throws Exception a PolicyRestrictionException.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        if (exception instanceof InvalidResponseException) {
            final var content = ((InvalidResponseException) exception).getResponse();
            if (log.isDebugEnabled()) {
                log.debug("Data could not be loaded. [content=({})]", content);
            }

            throw new PolicyRestrictionException(ErrorMessage.POLICY_RESTRICTION);
        }
    }

}
