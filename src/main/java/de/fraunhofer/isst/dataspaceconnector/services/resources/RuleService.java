package de.fraunhofer.isst.dataspaceconnector.services.resources;

import org.springframework.stereotype.Service;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import lombok.NoArgsConstructor;

/**
 * Handles the basic logic for contract rules.
 */
@Service
@NoArgsConstructor
public class RuleService extends BaseEntityService<ContractRule, ContractRuleDesc> { }
