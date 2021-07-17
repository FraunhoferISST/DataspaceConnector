package io.dataspaceconnector.camel.routes.handler;

import io.dataspaceconnector.camel.exception.ContractListEmptyException;
import io.dataspaceconnector.camel.exception.ContractRejectedException;
import io.dataspaceconnector.camel.exception.MalformedRuleException;
import io.dataspaceconnector.camel.exception.MissingRulesException;
import io.dataspaceconnector.camel.exception.MissingTargetInRuleException;
import io.dataspaceconnector.exception.MessageRequestException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractRequestHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(ResourceNotFoundException.class)
                .to("direct:handleResourceNotFoundException");
        onException(IllegalArgumentException.class)
                .to("direct:handleIllegalArgumentException");
        onException(MessageRequestException.class)
                .to("direct:handleMessagePayloadException");
        onException(MissingRulesException.class)
                .to("direct:handleMissingRulesException");
        onException(MissingTargetInRuleException.class)
                .to("direct:handleMissingTargetInRuleException");

        from("direct:contractRequestHandler")
                .routeId("contractRequestHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("ContractDeserializer")
                .process("ContractRuleListTransformer")
                .process("RuleListValidator")
                .process("ContractTargetRuleMapTransformer")
                .process("TargetRuleMapValidator")
                .doTry()
                    .process("RuleValidator")
                    .doCatch(MalformedRuleException.class)
                        .to("direct:handleMalformedRules")
                        .stop()
                    .doCatch(ContractListEmptyException.class)
                        .to("direct:handleContractListEmptyException")
                        .stop()
                    .doCatch(ContractRejectedException.class)
                        .process("RejectContractProcessor")
                        .stop()
                .end()
                .process("AcceptContractProcessor");
    }

}
