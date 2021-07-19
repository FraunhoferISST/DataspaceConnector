package io.dataspaceconnector.idscp.preparer;

import java.nio.charset.StandardCharsets;

import de.fraunhofer.iais.eis.ContractAgreement;
import io.dataspaceconnector.camel.dto.Request;
import io.dataspaceconnector.camel.util.ParameterUtils;
import org.apache.camel.Message;
import org.springframework.stereotype.Component;

/**
 * Prepares a request message for IDSCPv2 communication with a contract agreement as payload.
 */
@Component("ContractAgreementPreparer")
public class ContractAgreementPreparer extends Idscp2MappingProcessor {

    /**
     * Prepares a {@link Request} with a contract agreement as body for communication over IDSCPv2.
     * @param in the in-message of the exchange.
     */
    @Override
    protected void processInternal(final Message in) {
        final var request = in.getBody(Request.class);
        final var agreement = (ContractAgreement) request.getBody();

        in.setHeader(ParameterUtils.IDSCP_HEADER, request.getHeader());
        in.setBody(agreement.toRdf().getBytes(StandardCharsets.UTF_8));
    }

}
