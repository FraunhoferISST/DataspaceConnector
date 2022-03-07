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
package io.dataspaceconnector.service.message.handler;

import de.fraunhofer.iais.eis.Message;
import io.dataspaceconnector.common.ids.message.ClearingHouseService;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.Response;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * Logs IDS messages in the Clearing House.
 */
@Component("ClearingHouseLoggingProcessor")
@RequiredArgsConstructor
public class ClearingHouseLoggingProcessor implements Processor {
    /**
     * Service for ids log messages.
     */
    private final @NonNull ClearingHouseService clearingHouseSvc;

    /**
     * Processes the input. Extract the request/response ids message, create a LogMessage with the
     * ids message as payload, then send to the Clearing House.
     *
     * @param exchange the input.
     * @throws Exception if an error occurs.
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        RouteMsg<?, ?> msg = exchange.getIn().getBody(Request.class);
        if (msg == null) {
            msg = exchange.getIn().getBody(Response.class);
        }

        clearingHouseSvc.logIdsMessage((Message) msg.getHeader());
    }
}
