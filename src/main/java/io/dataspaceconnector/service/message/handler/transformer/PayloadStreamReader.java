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
package io.dataspaceconnector.service.message.handler.transformer;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.ids.messaging.handler.message.MessagePayload;
import io.dataspaceconnector.service.message.handler.dto.Request;
import io.dataspaceconnector.service.message.handler.dto.RouteMsg;
import io.dataspaceconnector.service.message.handler.transformer.base.IdsTransformer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Transform a {@link MessagePayload} body to a string for error handling, as in case of a
 * not parsable body, the payload is logged.
 */
@Component("PayloadStreamReader")
class PayloadStreamReader extends IdsTransformer<
        Request<? extends Message, MessagePayload, Optional<Jws<Claims>>>,
        RouteMsg<? extends Message, String>> {

    /**
     * Transforms the payload of the incoming RouteMsg from a MessagePayload to a string.
     *
     * @param msg the incoming message.
     * @return the transformed input.
     * @throws Exception if transformation fails.
     */
    @Override
    protected RouteMsg<? extends Message, String> processInternal(final Request<? extends Message,
            MessagePayload, Optional<Jws<Claims>>> msg) throws Exception {
        final var inputStream = msg.getBody().getUnderlyingInputStream();

        // Reset the stream so it can be read again.
        inputStream.reset();

        var payload = "Payload could not be read from request.";
        try (var reader = new BufferedReader(new InputStreamReader(inputStream,
                Charset.defaultCharset()))) {
            payload = reader.lines().parallel().collect(Collectors.joining("\n"));
        }

        return new Request<>(msg.getHeader(), payload, msg.getClaims());
    }
}
