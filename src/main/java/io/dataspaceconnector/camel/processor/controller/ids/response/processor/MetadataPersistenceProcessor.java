package io.dataspaceconnector.camel.processor.controller.ids.response.processor;

import java.net.URI;
import java.util.List;

import io.dataspaceconnector.camel.dto.Response;
import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.camel.util.ProcessorUtils;
import io.dataspaceconnector.service.EntityPersistenceService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Persists the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("MetadataPersistenceProcessor")
@RequiredArgsConstructor
public class MetadataPersistenceProcessor extends IdsResponseProcessor {

    /**
     * Service for persisting entities.
     */
    private final @NonNull EntityPersistenceService persistenceSvc;

    /**
     * Persists the metadata.
     * @param exchange the exchange.
     */
    @Override
    protected void processInternal(final Exchange exchange) {
        final var response = exchange.getIn().getBody(Response.class);
        final var map = ProcessorUtils.getResponseMap(response);

        final var artifacts = exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);
        final var download = exchange.getProperty(ParameterUtils.DOWNLOAD_PARAM, boolean.class);
        final var recipient = exchange.getProperty(ParameterUtils.RECIPIENT_PARAM, URI.class);

        persistenceSvc.saveMetadata(map, toUriList(artifacts), download, recipient);
    }

    @SuppressWarnings("unchecked")
    private static List<URI> toUriList(final List<?> list) {
        return (List<URI>) list;
    }
}
