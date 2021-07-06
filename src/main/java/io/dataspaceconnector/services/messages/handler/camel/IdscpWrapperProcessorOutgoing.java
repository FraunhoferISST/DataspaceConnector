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
package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.ids.messaging.response.ErrorResponse;
import io.dataspaceconnector.services.messages.handler.camel.dto.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component("idscpWrapperProcessorOutgoing")
@Log4j2
public class IdscpWrapperProcessorOutgoing implements Processor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var in = exchange.getIn();
        final var response = in.getBody(Response.class);

        if (response != null) {
            in.setHeader("idscp2-header", response.getHeader());
            in.setBody(response.getBody().getBytes());
        } else {
            final var rejection = in.getBody(ErrorResponse.class);
            in.setHeader("idscp2-header", rejection.getRejectionMessage());
            in.setBody(rejection.getErrorMessage().getBytes());
        }
    }
}
