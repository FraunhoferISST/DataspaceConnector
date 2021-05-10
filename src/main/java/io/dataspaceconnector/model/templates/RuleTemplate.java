package io.dataspaceconnector.model.templates;

import io.dataspaceconnector.model.ContractRuleDesc;
import lombok.*;

import java.net.URI;

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
