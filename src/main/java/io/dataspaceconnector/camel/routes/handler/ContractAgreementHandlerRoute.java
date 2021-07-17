package io.dataspaceconnector.camel.routes.handler;

import io.dataspaceconnector.camel.exception.UnconfirmedAgreementException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.MessageRequestException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractAgreementHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(MessageRequestException.class)
                .to("direct:handleMessagePayloadException");
        onException(ContractException.class)
                .to("direct:handleContractException");
        onException(UnconfirmedAgreementException.class)
                .to("direct:handleMessageProcessingFailedForAgreement");
        onException(ResourceNotFoundException.class)
                .to("direct:handleMessageProcessingFailedForAgreement");
        onException(IllegalArgumentException.class)
                .to("direct:handleIllegalArgumentException");

        from("direct:contractAgreementHandler")
                .routeId("contractAgreementHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("AgreementDeserializer")
                .process("AgreementComparisonProcessor");
    }

}
