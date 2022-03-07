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
package io.dataspaceconnector.service.message.builder.type;

import java.io.IOException;

import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageImpl;
import de.fraunhofer.iais.eis.RequestMessageBuilder;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.ids.messaging.common.SerializeException;
import de.fraunhofer.ids.messaging.protocol.multipart.parser.MultipartDatapart;
import de.fraunhofer.ids.messaging.util.IdsMessageUtils;
import io.dataspaceconnector.common.exception.ErrorMessage;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.message.ProcessCreationMessageDesc;
import io.dataspaceconnector.service.message.builder.type.base.AbstractMessageService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;

/**
 * Message service for ids request messages.
 */
@Service
public final class ProcessCreationRequestService
        extends AbstractMessageService<ProcessCreationMessageDesc> {

    /**
     * @throws IllegalArgumentException     if desc is null.
     * @throws ConstraintViolationException if security tokes is null or another error appears
     *                                      when building the message.
     */
    @Override
    public Message buildMessage(final ProcessCreationMessageDesc desc)
            throws ConstraintViolationException {
        Utils.requireNonNull(desc, ErrorMessage.DESC_NULL);

        final var connectorId = getConnectorService().getConnectorId();
        final var modelVersion = getConnectorService().getOutboundModelVersion();
        final var token = getConnectorService().getCurrentDat();

        Utils.requireNonNull(token, ErrorMessage.DAT_NULL);

        return new RequestMessageBuilder()
                ._issued_(IdsMessageUtils.getGregorianNow())
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._senderAgent_(connectorId)
                ._securityToken_(token)
                ._recipientConnector_(Util.asList(desc.getRecipient()))
                .build();
    }

    @Override
    protected Class<?> getResponseMessageType() {
        return MessageProcessedNotificationMessageImpl.class;
    }

    /**
     * Builds the multipart body for creating a process at the Clearing House. A custom build method
     * is required here, as the Clearing House expects the payload part to have content type
     * application/json.
     *
     * @param header the header.
     * @param payload the payload.
     * @return the multipart body.
     * @throws SerializeException if the multipart message could not be built.
     */
    @Override
    protected MultipartBody buildMultipartBody(final Message header, final Object payload)
            throws SerializeException {
        try {
            final var builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            // Add header part
            builder.addFormDataPart(MultipartDatapart.HEADER.toString(),
                    new Serializer().serialize(header));

            // Build and add payload part
            final var payloadPart = RequestBody.create(payload.toString(),
                    MediaType.parse("application/json"));
            builder.addFormDataPart(MultipartDatapart.PAYLOAD.toString(), "payload", payloadPart);

            return builder.build();
        } catch (IOException e) {
            throw new SerializeException(e);
        }
    }

}
