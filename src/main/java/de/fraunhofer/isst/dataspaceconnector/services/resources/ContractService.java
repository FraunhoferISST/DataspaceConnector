package de.fraunhofer.isst.dataspaceconnector.services.resources;

import org.springframework.stereotype.Service;

import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractDesc;
import lombok.NoArgsConstructor;

/**
 * Handles the basic logic for contracts.
 */
@Service
@NoArgsConstructor
public class ContractService extends BaseEntityService<Contract, ContractDesc> { }
