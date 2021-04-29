package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;

import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Describes a agreement and all its dependencies.
 */
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class AgreementTemplate {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Agreement parameters.
     */
    private @NonNull AgreementDesc desc;
}
