package io.dataspaceconnector.camel.routes.handler;

import io.dataspaceconnector.camel.exception.NoTransferContractException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.PolicyRestrictionException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PolicyValidationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(NoTransferContractException.class)
                .to("direct:handleNoTransferContractException");
        onException(ResourceNotFoundException.class, IllegalArgumentException.class)
                .to("direct:handleMessageProcessingFailedForArtifact");
        onException(PolicyRestrictionException.class)
                .to("direct:handlePolicyRestrictionException");
        onException(ContractException.class)
                .to("direct:handleInvalidTransferContract");

        from("direct:policyCheck")
                .routeId("policyCheck")
                .process("PolicyValidator");
    }

}
