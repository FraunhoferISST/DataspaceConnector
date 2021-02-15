package de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backendtofrontend;

import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.services.resources.v2.backend.ResourceContractLinker;
import org.springframework.stereotype.Service;

@Service
public final class BFFResourceContractLinker extends CommonUniDirectionalLinkerService<ResourceContractLinker<OfferedResource>> {
}
