package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.ContractDesc;
import lombok.*;

import java.net.URI;
import java.util.List;

/**
 * Describes a contract and all its dependencies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class ContractTemplate {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Contract parameters.
     */
    @Setter(AccessLevel.NONE)
    private @NonNull ContractDesc desc;

    /**
     * List of rule templates.
     */
    private List<RuleTemplate> rules;
}
