package de.fraunhofer.isst.dataspaceconnector.view;

import de.fraunhofer.isst.dataspaceconnector.controller.ExampleController;
import de.fraunhofer.isst.dataspaceconnector.controller.MainController;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ViewEqualsTests {

    private final Link link1 = linkTo(MainController.class).withSelfRel();
    private final Link link2 = linkTo(ExampleController.class).withSelfRel();

    @Test
    public void verifyEquals_agreementView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(AgreementView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_artifactView_passesVerification() {
       EqualsVerifier.simple()
                .forClass(ArtifactView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_catalogView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(CatalogView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_contractRuleView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(ContractRuleView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_contractView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(ContractView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_offeredResourceView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(OfferedResourceView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_representationView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(RepresentationView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_requestedResourceView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(RequestedResourceView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

}
