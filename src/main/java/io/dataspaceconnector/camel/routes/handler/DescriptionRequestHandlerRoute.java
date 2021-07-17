package io.dataspaceconnector.camel.routes.handler;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.exception.InvalidResourceException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import io.dataspaceconnector.exception.SelfLinkCreationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class DescriptionRequestHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(ResourceNotFoundException.class, InvalidResourceException.class)
                .to("direct:handleResourceNotFoundException");
        onException(IllegalStateException.class, ConstraintViolationException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(SelfLinkCreationException.class)
                .to("direct:handleSelfLinkCreationException");

        from("direct:descriptionRequestHandler")
                .routeId("descriptionRequestHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .choice()
                    .when(simple("${body.getHeader().getRequestedElement()} == null"))
                        .process("SelfDescription")
                    .otherwise()
                        .process("ResourceDescription");
    }

}
