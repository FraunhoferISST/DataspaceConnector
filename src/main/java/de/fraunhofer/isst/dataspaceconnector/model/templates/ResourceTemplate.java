package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import lombok.Data;

import java.util.List;

@Data
public class ResourceTemplate<D extends AbstractDescription<?>> {
    private D desc;
    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
