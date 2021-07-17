package io.dataspaceconnector.camel.routes.handler;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class NotificationHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(IllegalStateException.class, ConstraintViolationException.class)
                .to("direct:handleResponseMessageBuilderException");

        from("direct:notificationMsgHandler")
                .routeId("notificationHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("ProcessedNotification");
    }

}
