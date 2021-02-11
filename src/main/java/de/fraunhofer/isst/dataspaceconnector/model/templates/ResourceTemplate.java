package de.fraunhofer.isst.dataspaceconnector.model.templates;

import de.fraunhofer.isst.dataspaceconnector.model.ResourceDesc;
import lombok.Data;

import java.util.List;

@Data
public class ResourceTemplate {
    private ResourceDesc desc;
    private List<RepresentationTemplate> representations;
    private List<ContractTemplate> contracts;
}
