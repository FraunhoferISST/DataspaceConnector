package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import lombok.Data;

import java.util.List;

@Data
public class ContractTemplate {
    private ContractDesc desc;
    private List<RuleTemplate> rules;
}
