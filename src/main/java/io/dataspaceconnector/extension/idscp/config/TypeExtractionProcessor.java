package io.dataspaceconnector.extension.idscp.config;

import de.fraunhofer.iais.eis.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeExtractionProcessor implements Processor {
    private final Logger LOG = LoggerFactory.getLogger(TypeExtractionProcessor.class);
    @SuppressWarnings("FieldCanBeLocal")
    private final String IDSCP2_HEADER = "idscp2-header";
    @SuppressWarnings("FieldCanBeLocal")
    private final String IDS_TYPE = "ids-type";

    @Override
    public void process(Exchange exchange) throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("[IN] " + TypeExtractionProcessor.class.getSimpleName());
        }
        final var header = exchange.getMessage().getHeader(IDSCP2_HEADER, Message.class);
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
        if (LOG.isDebugEnabled()) {
            LOG.debug("Detected ids-type: {}", messageType);
        }
        exchange.setProperty(IDS_TYPE, messageType);
    }
}
