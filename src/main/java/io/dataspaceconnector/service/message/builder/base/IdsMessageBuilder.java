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
package io.dataspaceconnector.service.message.builder.base;

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.util.Optional;

/**
 * Superclass for all processors that build IDS messages and their payload according to input
 * parameters stored as Exchange properties.
 *
 * @param <H> the type of IDS message.
 * @param <B> the type of payload.
 */
public abstract class IdsMessageBuilder<H extends Message, B> implements Processor {

    /**
     * Override of the {@link Processor}'s process method. Calls the implementing class's
     * processInternal method and sets the result as the {@link Exchange}'s body.
     *
     * @param exchange the exchange.
     * @throws Exception if building the message or payload fails.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var request = processInternal(exchange);
        exchange.getIn().setBody(request);
    }

    /**
     * Creates a request DTO with the desired message type as header and the appropriate payload.
     * To be implemented by sub classes.
     *
     * @param exchange the exchange.
     * @return the {@link Request}.
     */
    protected abstract Request<H, B, Optional<Jws<Claims>>> processInternal(Exchange exchange);

}
