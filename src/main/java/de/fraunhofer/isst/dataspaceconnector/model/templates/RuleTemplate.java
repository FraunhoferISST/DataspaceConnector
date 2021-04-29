package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Describes a contract rule and all its dependencies.
 */
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class RuleTemplate {

    /**
     * Old remote id.
     */
    private URI oldRemoteId;

    /**
     * Rule parameters.
     */
    private @NonNull ContractRuleDesc desc;
}
