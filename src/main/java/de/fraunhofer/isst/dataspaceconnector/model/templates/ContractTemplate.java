package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractTemplate {
    private ContractDesc desc;
    private List<RuleTemplate> rules;
}
