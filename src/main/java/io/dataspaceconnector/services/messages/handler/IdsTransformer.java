package io.dataspaceconnector.services.messages.handler;

import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.spi.annotations.Component;

import de.fraunhofer.iais.eis.ContractRequestMessageImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayload;
import de.fraunhofer.iais.eis.ContractRequest;
import io.dataspaceconnector.services.ids.DeserializationService;
import io.dataspaceconnector.utils.MessageUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
        return new Request<ContractRequestMessageImpl, ContractRequest>(msg.getHeader(), contract);
    }

}

@org.springframework.stereotype.Component("ResourceDeserializer")
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
