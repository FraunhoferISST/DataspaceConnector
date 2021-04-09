package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;

import de.fraunhofer.isst.dataspaceconnector.model.AgreementDesc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class AgreementTemplate {
    private URI oldRemoteId;
    private @NonNull AgreementDesc desc;
}
