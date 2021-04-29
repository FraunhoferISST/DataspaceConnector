package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
