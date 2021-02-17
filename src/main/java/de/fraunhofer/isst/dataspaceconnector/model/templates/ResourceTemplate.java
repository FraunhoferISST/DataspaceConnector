package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.BaseDescription;
import lombok.Data;

import java.util.List;

@Data
public class ResourceTemplate<D extends BaseDescription<?>> {
    private D desc;
    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
