package io.dataspaceconnector.camel.routes.handler;

import java.io.IOException;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.camel.exception.NoRequestedArtifactException;
import io.dataspaceconnector.exception.InvalidInputException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ArtifactRequestHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(NoRequestedArtifactException.class)
                .to("direct:handleNoRequestedArtifactException");
        onException(InvalidInputException.class)
                .to("direct:handleInvalidQueryInputException");
        onException(IOException.class, ConstraintViolationException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(Exception.class)
                .to("direct:handleDataRetrievalError");

        from("direct:artifactRequestHandler")
                .routeId("artifactRequestHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("RequestedArtifactValidator")
                .choice()
                    .when(simple("${bean:connectorConfiguration.isPolicyNegotiation} == true"))
                        .to("direct:policyCheck")
                .end()
                .process("DataRequestProcessor");
    }

}
