package de.fraunhofer.isst.dataspaceconnector.controller.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.view.ArtifactView;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractRuleView;
import de.fraunhofer.isst.dataspaceconnector.model.view.ContractView;
import de.fraunhofer.isst.dataspaceconnector.model.view.OfferedResourceView;
import de.fraunhofer.isst.dataspaceconnector.model.view.RepresentationView;
import de.fraunhofer.isst.dataspaceconnector.services.resources.RelationshipServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

public final class RelationshipControllers {

    @RestController
    @RequestMapping("/api/rules/{id}/contracts")
    @Tag(name = "Rules", description = "Endpoints for linking rules to contracts")
    public static class RulesToContracts extends BaseResourceChildController<RelationshipServices.RuleContractLinker, ContractRule, ContractRuleView> {
    }

    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = "Artifacts", description = "Endpoints for linking artifacts to representations")
    public static class ArtifactsToRepresentations extends BaseResourceChildController<RelationshipServices.ArtifactRepresentationLinker, Artifact, ArtifactView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/offers")
    @Tag(name = "Representations", description = "Endpoints for linking representations to offered resources")
    public static class RepresentationsToOfferedResources extends BaseResourceChildController<RelationshipServices.RepresentationOfferedResourceLinker, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/requests")
    @Tag(name = "Representations", description = "Endpoints for linking representations to requested resources")
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<RelationshipServices.RepresentationOfferedResourceLinker, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/offers/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking offered resources to catalogs")
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<RelationshipServices.OfferedResourceCatalogLinker, OfferedResource, OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/requests/{id}/catalogs")
    @Tag(name = "Resources", description = "Endpoints for linking requested resources to catalogs")
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<RelationshipServices.RequestedResourceCatalogLinker, RequestedResource, OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/offers")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to offers")
    public static class ContractsToOfferedResources extends BaseResourceChildController<RelationshipServices.ContractOfferedResourceLinker, Contract, ContractView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/requests")
    @Tag(name = "Contracts", description = "Endpoints for linking contracts to requests")
    public static class ContractsToRequestedResources extends BaseResourceChildController<RelationshipServices.ContractRequestedResourceLinker, Contract, ContractView> {
    }
}
