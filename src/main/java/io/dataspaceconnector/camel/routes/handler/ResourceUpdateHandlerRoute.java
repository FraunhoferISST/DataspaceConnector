package io.dataspaceconnector.camel.routes.handler;

import java.io.IOException;

import io.dataspaceconnector.camel.exception.DeserializationException;
import io.dataspaceconnector.camel.exception.InvalidAffectedResourceException;
import io.dataspaceconnector.camel.exception.MissingPayloadException;
import io.dataspaceconnector.camel.exception.NoAffectedResourceException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ResourceUpdateHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(NoAffectedResourceException.class)
                .to("direct:handleNoAffectedResourceException");
        onException(InvalidAffectedResourceException.class)
                .to("direct:handleInvalidAffectedResourceException");
        onException(IllegalStateException.class)
                .to("direct:handleResponseMessageBuilderException");
        onException(IOException.class, IllegalArgumentException.class)
                .to("direct:handleMessagePayloadException");

        from("direct:resourceUpdateHandler")
                .routeId("resourceUpdateHandler")
                .transacted("transactionPolicy")
                .to("direct:ids-validation")
                .process("AffectedResourceValidator")
                .doTry()
                    .process("ResourceDeserializer")
                    .doCatch(DeserializationException.class)
                        .to("direct:handleWrappedIllegalArgumentException")
                        .stop()
                    .doCatch(MissingPayloadException.class)
                        .to("direct:handleMissingPayloadException")
                        .stop()
                .end()
                .process("CorrectAffectedResourceValidator")
                .process("ResourceUpdateProcessor");
    }

}
