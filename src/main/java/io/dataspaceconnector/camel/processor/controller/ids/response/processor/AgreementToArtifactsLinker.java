package io.dataspaceconnector.camel.processor.controller.ids.response.processor;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import io.dataspaceconnector.camel.util.ParameterUtils;
import io.dataspaceconnector.service.EntityUpdateService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Links the contract agreement received as the response to a ContractRequestMessage to the
 * artifacts created from the metadata received as the response to a DescriptionRequestMessage.
 */
@Component("AgreementToArtifactsLinker")
@RequiredArgsConstructor
public class AgreementToArtifactsLinker extends IdsResponseProcessor {

    /**
     * Service for updating database entities.
     */
    private final @NonNull EntityUpdateService updateService;

    /**
     * Links the contract agreement to the artifacts.
     * @param exchange the exchange.
     * @throws Exception if linking the agreement to the artifacts fails.
     */
    @Override
    protected void processInternal(final Exchange exchange) throws Exception {
        final var agreementId = exchange.getProperty(ParameterUtils.AGREEMENT_ID_PARAM, UUID.class);
        final var artifacts = exchange.getProperty(ParameterUtils.ARTIFACTS_PARAM, List.class);

        updateService.linkArtifactToAgreement(toUriList(artifacts), agreementId);
    }

    @SuppressWarnings("unchecked")
    private static List<URI> toUriList(final List<?> list) {
        return (List<URI>) list;
    }
}
