/*
 * Copyright 2020-2022 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.resource.view;

import io.dataspaceconnector.controller.policy.ExampleController;
import io.dataspaceconnector.controller.MainController;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementView;
import io.dataspaceconnector.controller.resource.view.artifact.ArtifactView;
import io.dataspaceconnector.controller.resource.view.catalog.CatalogView;
import io.dataspaceconnector.controller.resource.view.contract.ContractView;
import io.dataspaceconnector.controller.resource.view.representation.RepresentationView;
import io.dataspaceconnector.controller.resource.view.resource.OfferedResourceView;
import io.dataspaceconnector.controller.resource.view.resource.RequestedResourceView;
import io.dataspaceconnector.controller.resource.view.rule.ContractRuleView;
import io.dataspaceconnector.controller.resource.view.subscription.SubscriptionView;
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
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_artifactView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(ArtifactView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_catalogView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(CatalogView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_contractRuleView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(ContractRuleView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_contractView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(ContractView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_offeredResourceView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(OfferedResourceView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_representationView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(RepresentationView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_requestedResourceView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(RequestedResourceView.class)
                .withPrefabValues(Link.class, link1, link2)
                .withNonnullFields("links")
                .verify();
    }

    @Test
    public void verifyEquals_subscriberView_passesVerification() {
        EqualsVerifier.simple()
                .forClass(SubscriptionView.class)
                .withPrefabValues(Link.class, link1 ,link2)
                .withNonnullFields("links")
                .verify();
    }

}
