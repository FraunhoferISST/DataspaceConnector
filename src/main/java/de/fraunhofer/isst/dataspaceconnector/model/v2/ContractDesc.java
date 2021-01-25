package de.fraunhofer.isst.dataspaceconnector.model.v2;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContractDesc extends BaseDescription<Contract> {
    private String title;
}
