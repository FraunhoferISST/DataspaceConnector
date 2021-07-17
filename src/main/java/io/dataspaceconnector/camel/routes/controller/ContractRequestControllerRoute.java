package io.dataspaceconnector.camel.routes.controller;

import java.io.IOException;
import javax.persistence.PersistenceException;

import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import io.dataspaceconnector.camel.exception.InvalidResponseException;
import io.dataspaceconnector.exception.ContractException;
import io.dataspaceconnector.exception.InvalidInputException;
import io.dataspaceconnector.exception.MessageException;
import io.dataspaceconnector.exception.MessageResponseException;
import io.dataspaceconnector.exception.ResourceNotFoundException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ContractRequestControllerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(InvalidInputException.class)
                .to("direct:handleInvalidInputException");
        onException(ConstraintViolationException.class)
                .to("direct:handleFailedToBuildContractRequest");
        onException(InvalidResponseException.class)
                .to("direct:handleInvalidResponseException");
        onException(MessageException.class)
                .to("direct:handleIdsMessageFailed");
        onException(MessageResponseException.class, ContractException.class,
                IllegalArgumentException.class)
                .to("direct:handleReceivedInvalidResponse");
        onException(PersistenceException.class)
                .to("direct:handlePersistenceException");
        onException(IOException.class, ResourceNotFoundException.class,
                MessageResponseException.class)
                .log(LoggingLevel.DEBUG, "Could not save data for artifact. "
                        + "[artifact=(${exchangeProperty.currentArtifact}), "
                        + "exception=(${exception.message})]");

        from("direct:contractRequestSender")
                .routeId("contractRequestSender")
                .process("RuleListInputValidator")
                .process("ContractRequestMessageBuilder")
                .process("ContractRequestPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter")
                .process("ContractResponseValidator")
                .process("ContractAgreementValidator")
                .process("ContractAgreementMessageBuilder")
                .process("ContractAgreementPreparer")
                .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                .process("ResponseToDtoConverter")
                .process("ContractAgreementResponseValidator")
                .process("ContractAgreementPersistenceProcessor")
                .loop(simple("${exchangeProperty.resources.size()}"))
                    .process("DescriptionRequestMessageBuilder")
                    .process("RequestWithoutPayloadPreparer")
                    .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                    .process("ResponseToDtoConverter")
                    .process("DescriptionResponseValidator")
                    .process("MetadataPersistenceProcessor")
                .end()
                .process("AgreementToArtifactsLinker")
                .choice()
                    .when(simple("${exchangeProperty.download}"))
                        .loop(simple("${exchangeProperty.artifacts.size()}"))
                            .process("ArtifactRequestMessageBuilder")
                            .process("RequestWithoutPayloadPreparer")
                            .toD("idscp2client://${exchangeProperty.recipient}?awaitResponse=true&sslContextParameters=#serverSslContext&useIdsMessages=true")
                            .process("ResponseToDtoConverter")
                            .process("ArtifactResponseValidator")
                            .process("DataPersistenceProcessor")
                        .end()
                    .endChoice()
                .end();


    }

}
