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
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class RulesToContracts extends BaseResourceChildController<RelationshipServices.RuleContractLinker, ContractRule, ContractRuleView> {
    }

    @RestController
    @RequestMapping("/api/artifacts/{id}/representations")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class ArtifactsToRepresentations extends BaseResourceChildController<RelationshipServices.ArtifactRepresentationLinker, Artifact, ArtifactView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/offered")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class RepresentationsToOfferedResources extends BaseResourceChildController<RelationshipServices.RepresentationOfferedResourceLinker, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/representations/{id}/requested")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class RepresentationsToRequestedResources extends BaseResourceChildController<RelationshipServices.RepresentationOfferedResourceLinker, Representation, RepresentationView> {
    }

    @RestController
    @RequestMapping("/api/offered/{id}/catalogs")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class OfferedResourcesToCatalogs extends BaseResourceChildController<RelationshipServices.OfferedResourceCatalogLinker, OfferedResource, OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/requested/{id}/catalogs")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class RequestedResourcesToCatalogs extends BaseResourceChildController<RelationshipServices.RequestedResourceCatalogLinker, RequestedResource, OfferedResourceView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/offered")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class ContractsToOfferedResources extends BaseResourceChildController<RelationshipServices.ContractOfferedResourceLinker, Contract, ContractView> {
    }

    @RestController
    @RequestMapping("/api/contracts/{id}/requested")
    @Tag(name = "Linker", description = "Endpoints for linking a base resource and its children")
    public static class ContractsToRequestedResources extends BaseResourceChildController<RelationshipServices.ContractRequestedResourceLinker, Contract, ContractView> {
    }
}
