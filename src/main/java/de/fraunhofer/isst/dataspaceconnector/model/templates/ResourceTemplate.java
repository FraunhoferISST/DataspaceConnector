package de.fraunhofer.isst.dataspaceconnector.model.templates;

import java.util.List;

import de.fraunhofer.isst.dataspaceconnector.model.AbstractDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTemplate<D extends AbstractDescription<?>> {
    private D desc;
    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
