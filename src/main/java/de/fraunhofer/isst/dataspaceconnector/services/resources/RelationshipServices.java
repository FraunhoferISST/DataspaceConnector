package de.fraunhofer.isst.dataspaceconnector.services.resources;

import de.fraunhofer.isst.dataspaceconnector.model.Agreement;
import de.fraunhofer.isst.dataspaceconnector.model.Artifact;
import de.fraunhofer.isst.dataspaceconnector.model.Catalog;
import de.fraunhofer.isst.dataspaceconnector.model.Contract;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public final class RelationshipServices {

    @Service
    @NoArgsConstructor
    public static class RuleContractLinker extends BaseUniDirectionalLinkerService<ContractRule, Contract,
            RuleService, ContractService> {

        @Override
        protected List<Contract> getInternal(final ContractRule owner) {
            return owner.getContracts();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ArtifactRepresentationLinker extends BaseUniDirectionalLinkerService<Artifact, Representation, ArtifactService, RepresentationService> {

        @Override
        protected List<Representation> getInternal(final Artifact owner) {
            return owner.getRepresentations();
        }
    }

    @Service
    @NoArgsConstructor
    public static class RepresentationOfferedResourceLinker extends BaseUniDirectionalLinkerService<Representation, OfferedResource, RepresentationService, OfferedResourceService> {

        @Override
        protected List<OfferedResource> getInternal(final Representation owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }

    @Service
    @NoArgsConstructor
    public static class RepresentationRequestedResourceLinker extends BaseUniDirectionalLinkerService<Representation, RequestedResource, RepresentationService, RequestedResourceService> {

        @Override
        protected List<RequestedResource> getInternal(final Representation owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }

    @Service
    @NoArgsConstructor
    public static class OfferedResourceCatalogLinker extends BaseUniDirectionalLinkerService<OfferedResource, Catalog, OfferedResourceService, CatalogService> {

        @Override
        protected List<Catalog> getInternal(final OfferedResource owner) {
            return owner.getCatalogs();
        }
    }

    @Service
    @NoArgsConstructor
    public static class RequestedResourceCatalogLinker extends BaseUniDirectionalLinkerService<RequestedResource, Catalog, RequestedResourceService, CatalogService> {

        @Override
        protected List<Catalog> getInternal(final RequestedResource owner) {
            return owner.getCatalogs();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ContractOfferedResourceLinker extends BaseUniDirectionalLinkerService<Contract, OfferedResource, ContractService, OfferedResourceService> {

        @Override
        protected List<OfferedResource> getInternal(final Contract owner) {
            return (List<OfferedResource>) (List<?>) owner.getResources();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ContractRequestedResourceLinker extends BaseUniDirectionalLinkerService<Contract, RequestedResource, ContractService, RequestedResourceService> {

        @Override
        protected List<RequestedResource> getInternal(final Contract owner) {
            return (List<RequestedResource>) (List<?>) owner.getResources();
        }
    }

    @Service
    @NoArgsConstructor
    public static class AgreementArtifactLinker extends BaseUniDirectionalLinkerService<Agreement, Artifact, AgreementService, ArtifactService> {

        @Override
        protected List<Artifact> getInternal(final Agreement owner) {
            return owner.getArtifacts();
        }
    }

    @Service
    @NoArgsConstructor
    public static class ArtifactAgreementLinker extends BaseUniDirectionalLinkerService<Artifact, Agreement, ArtifactService, AgreementService> {

        @Override
        protected List<Agreement> getInternal(final Artifact owner) {
            return owner.getAgreements();
        }
    }
}
