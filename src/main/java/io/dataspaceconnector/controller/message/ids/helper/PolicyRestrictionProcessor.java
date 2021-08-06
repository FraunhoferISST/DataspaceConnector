/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.message.ids.helper;

import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.PolicyRestrictionException;
import io.dataspaceconnector.controller.message.ids.helper.base.IdsHelperProcessor;
import io.dataspaceconnector.service.message.handler.exception.InvalidResponseException;
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
