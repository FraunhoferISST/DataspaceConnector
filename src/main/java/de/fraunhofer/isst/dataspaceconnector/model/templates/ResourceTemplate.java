package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;
import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ResourceTemplate<D extends AbstractDescription<?>> {
    private          URI oldRemoteId;
    @Setter(AccessLevel.NONE)
    private @NonNull D   desc;

    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
