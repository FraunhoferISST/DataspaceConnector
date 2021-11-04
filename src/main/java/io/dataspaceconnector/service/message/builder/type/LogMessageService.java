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
package io.dataspaceconnector.service.message.builder.type;

import de.fraunhofer.iais.eis.LogMessageBuilder;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.exception.MessageException;
import io.dataspaceconnector.common.exception.MessageResponseException;
import io.dataspaceconnector.common.exception.PolicyExecutionException;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.message.LogMessageDesc;
import io.dataspaceconnector.service.message.builder.type.base.AbstractMessageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URI;

/**
 * Message service for ids log messages.
 */
@Log4j2
@Service
public final class LogMessageService extends AbstractMessageService<LogMessageDesc> {

    /**
     * @throws IllegalArgumentException     if desc is null.
     * @throws ConstraintViolationException if security tokes is null or another error appears
     *                                      when building the message.
     */
    @Override
    public Message buildMessage(final LogMessageDesc desc) throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        Utils.requireNonNull(token, ErrorMessage.DAT_NULL);

        final var recipient = desc.getRecipient();

        return new LogMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(recipient))
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return null;
    }

    /**
     * Send a message to the clearing house. Allow the access only if that operation was successful.
     *
     * @param recipient The message's recipient.
     * @param logItem   The item that should be logged.
     * @throws PolicyExecutionException if the access could not be successfully logged.
     */
    public void sendMessage(final URI recipient, final Object logItem)
            throws PolicyExecutionException {
        try {
            final var response = send(new LogMessageDesc(recipient), logItem);
            if (response == null) {
                if (log.isDebugEnabled()) {
                    log.debug("No response received.");
                }
                throw new PolicyExecutionException("Log message has no valid response.");
            }
        } catch (MessageException | MessageResponseException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to send log message. [exception=({})]", e.getMessage(), e);
            }
            throw new PolicyExecutionException("Log message could not be sent.");
        }
    }
}
