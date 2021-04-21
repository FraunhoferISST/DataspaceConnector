package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.URI;

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
