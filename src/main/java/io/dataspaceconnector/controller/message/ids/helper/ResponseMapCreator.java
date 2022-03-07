/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
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

import io.dataspaceconnector.controller.message.ids.helper.base.IdsHelperProcessor;
import io.dataspaceconnector.service.message.handler.exception.InvalidResponseException;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Sets a map representing an invalid response that has been received as the exchange message's
 * body.
 */
@Component("ResponseMapCreator")
public class ResponseMapCreator extends IdsHelperProcessor {

    /**
     * Sets the response map from the InvalidResponseException thrown as the exchange message's
     * body, so that it can be processed by the corresponding ResponseUtils method for this
     * type of exception.
     *
     * @param exchange the exchange.
     * @throws Exception if an error occurs.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var exception = exchange
                .getProperty(Exchange.EXCEPTION_CAUGHT, InvalidResponseException.class);
        exchange.getIn().setBody(exception.getResponse());
    }

}
