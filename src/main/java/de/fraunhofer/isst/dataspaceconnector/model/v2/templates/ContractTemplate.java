package de.fraunhofer.isst.dataspaceconnector.model.v2.templates;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ContractDesc;
import lombok.Data;

import java.util.List;

@Data
public class ContractTemplate {
    private ContractDesc desc;
    private List<RuleTemplate> rules;
}
