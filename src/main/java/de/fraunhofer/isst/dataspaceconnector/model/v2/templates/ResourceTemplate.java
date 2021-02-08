package de.fraunhofer.isst.dataspaceconnector.model.v2.templates;

import de.fraunhofer.isst.dataspaceconnector.model.v2.ResourceDesc;
import lombok.Data;

import java.util.List;

@Data
public class ResourceTemplate {
    private ResourceDesc desc;
    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
