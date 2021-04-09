package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class RuleTemplate {
    private URI oldRemoteId;
    private @NonNull ContractRuleDesc desc;
}
