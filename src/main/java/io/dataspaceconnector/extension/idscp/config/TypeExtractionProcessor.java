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
package io.dataspaceconnector.extension.idscp.config;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractOfferMessage;
import de.fraunhofer.iais.eis.ContractRejectionMessage;
import de.fraunhofer.iais.eis.ContractRequestMessage;
import de.fraunhofer.iais.eis.ContractResponseMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Processor for identifying the kind of IDS message and makes it available via a Camel property.
 */
@Log4j2
public final class TypeExtractionProcessor implements Processor {
    /**
     * Name/Key of IDSCP2 header field.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String idscp2HeaderName = "idscp2-header";
    /**
     * Name/Key of IDS message type property.
     */
    @SuppressWarnings("FieldCanBeLocal")
    private final String idsTypePropertyName = "ids-type";

    /**
     * Processor that identifies the kind of IDS message
     * and makes it available via a Camel property.
     * @param exchange The Camel Exchange to be processed
     * @throws Exception On error
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        final var header = exchange.getMessage().getHeader(idscp2HeaderName, Message.class);
        final String messageType;
        if (header instanceof ArtifactRequestMessage) {
            messageType = ArtifactRequestMessage.class.getSimpleName();
        } else if (header instanceof ArtifactResponseMessage) {
            messageType = ArtifactResponseMessage.class.getSimpleName();
        } else if (header instanceof ContractRequestMessage) {
            messageType = ContractRequestMessage.class.getSimpleName();
        } else if (header instanceof ContractResponseMessage) {
            messageType = ContractResponseMessage.class.getSimpleName();
        } else if (header instanceof ContractOfferMessage) {
            messageType = ContractOfferMessage.class.getSimpleName();
        } else if (header instanceof ContractAgreementMessage) {
            messageType = ContractAgreementMessage.class.getSimpleName();
        } else if (header instanceof ContractRejectionMessage) {
            messageType = ContractRejectionMessage.class.getSimpleName();
        } else if (header instanceof ResourceUpdateMessage) {
            messageType = ResourceUpdateMessage.class.getSimpleName();
        } else if (header instanceof RejectionMessage) {
            messageType = RejectionMessage.class.getSimpleName();
        } else if (header != null) {
            messageType = header.getClass().getSimpleName();
        } else {
            messageType = "null";
        }
        if (log.isDebugEnabled()) {
            log.debug("Detected ids-type: {}", messageType);
        }
        exchange.setProperty(idsTypePropertyName, messageType);
    }
}
