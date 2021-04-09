package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.net.URI;
import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
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
public class ContractTemplate {
    private          URI          oldRemoteId;
    @Setter(AccessLevel.NONE)
    private @NonNull ContractDesc desc;
    private          List<RuleTemplate> rules;
}
