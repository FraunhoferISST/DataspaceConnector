package io.dataspaceconnector.services.messages.handler.camel;

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementMessageImpl;
import de.fraunhofer.iais.eis.ContractRequest;
import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

public abstract class IdsTransformer<I, O> implements Processor {

    @Override
    @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(processInternal((I)exchange.getIn().getBody(RouteMsg.class)));
    }

    protected abstract O processInternal(I msg) throws Exception;
}

@Component("ContractDeserializer")
@RequiredArgsConstructor
class ContractTransformer extends IdsTransformer<RouteMsg<ContractRequestMessageImpl, MessagePayload>,
                                                 RouteMsg<ContractRequestMessageImpl, ContractRequest>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    @Override
    protected RouteMsg<ContractRequestMessageImpl, ContractRequest> processInternal(RouteMsg<ContractRequestMessageImpl, MessagePayload> msg) throws Exception {
        final var contract = deserializationService.getContractRequest(MessageUtils.getPayloadAsString(msg.getBody()));
        return new Request<>(msg.getHeader(), contract);
    }

}

@Component("ResourceDeserializer")
@RequiredArgsConstructor
class ResourceTransformer extends IdsTransformer<RouteMsg<ResourceUpdateMessageImpl, MessagePayload>,
                                                 RouteMsg<ResourceUpdateMessageImpl, Resource>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    @Override
    protected RouteMsg<ResourceUpdateMessageImpl, Resource> processInternal(final RouteMsg<ResourceUpdateMessageImpl, MessagePayload> msg) throws Exception {
        final var resource = deserializationService.getResource(MessageUtils.getStreamAsString(msg.getBody()));
        return new Request<>(msg.getHeader(), resource);
    }

}

@Component("AgreementDeserializer")
@RequiredArgsConstructor
class ContractAgreementTransformer extends IdsTransformer<RouteMsg<ContractAgreementMessageImpl, MessagePayload>,
                                                          RouteMsg<ContractAgreementMessageImpl, ContractAgreement>> {

    /**
     * Service for ids deserialization.
     */
    private final @NonNull DeserializationService deserializationService;

    @Override
    protected RouteMsg<ContractAgreementMessageImpl, ContractAgreement> processInternal(final RouteMsg<ContractAgreementMessageImpl, MessagePayload> msg) throws Exception {
        final var agreement = deserializationService.getContractAgreement(MessageUtils.getPayloadAsString(msg.getBody()));
        return new Request<>(msg.getHeader(), agreement);
    }

}
