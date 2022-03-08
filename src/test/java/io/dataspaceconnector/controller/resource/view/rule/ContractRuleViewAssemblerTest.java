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
package io.dataspaceconnector.controller.resource.view.rule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

import io.dataspaceconnector.controller.resource.relation.RulesToContractsController;
import io.dataspaceconnector.controller.resource.type.RuleController;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@SpringBootTest(classes = {
        ContractRuleViewAssembler.class
})
public class ContractRuleViewAssemblerTest {

    @Autowired
    private ContractRuleViewAssembler contractRuleViewAssembler;

    @SpyBean
    private ContractRuleFactory contractRuleFactory;

    @Test
    public void getSelfLink_inputNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RuleController.class
                .getAnnotation(RequestMapping.class).value()[0];
        final var rel = "self";

        /* ACT */
        final var result = contractRuleViewAssembler.getSelfLink(null);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals(rel, result.getRel().value());
    }

    @Test
    public void getSelfLink_validInput_returnSelfLink() {
        /* ARRANGE */
        final var contractRuleId = UUID.randomUUID();
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        final var path = RuleController.class
                .getAnnotation(RequestMapping.class).value()[0];

        /* ACT */
        final var result = contractRuleViewAssembler.getSelfLink(contractRuleId);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + contractRuleId, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void toModel_inputNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> contractRuleViewAssembler.toModel(null));
    }

    @Test
    public void toModel_validInput_returnContractRuleView() {
        /* ARRANGE */
        final var contractRule = getContractRule();

        /* ACT */
        final var result = contractRuleViewAssembler.toModel(contractRule);

        /* ASSERT */
        assertNotNull(result);
        Assertions.assertEquals(contractRule.getTitle(), result.getTitle());
        Assertions.assertEquals(contractRule.getValue(), result.getValue());
        Assertions.assertEquals(contractRule.getCreationDate(), result.getCreationDate());
        Assertions.assertEquals(contractRule.getModificationDate(), result.getModificationDate());
        Assertions.assertEquals(contractRule.getAdditional(), result.getAdditional());

        final var selfLink = result.getLink("self");
        assertTrue(selfLink.isPresent());
        assertNotNull(selfLink.get());
        assertEquals(getContractRuleLink(contractRule.getId()), selfLink.get().getHref());

        final var contractsLink = result.getLink("contracts");
        assertTrue(contractsLink.isPresent());
        assertNotNull(contractsLink.get());
        assertEquals(getContractRuleContractsLink(contractRule.getId()),
                contractsLink.get().getHref());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    private ContractRule getContractRule() {
        final var desc = new ContractRuleDesc();
        desc.setTitle("title");
        desc.setValue("value");
        final var contractRule = contractRuleFactory.create(desc);

        final var date = ZonedDateTime.now(ZoneOffset.UTC);
        final var additional = new HashMap<String, String>();
        additional.put("key", "value");

        ReflectionTestUtils.setField(contractRule, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(contractRule, "creationDate", date);
        ReflectionTestUtils.setField(contractRule, "modificationDate", date);
        ReflectionTestUtils.setField(contractRule, "additional", additional);

        return contractRule;
    }

    private String getContractRuleLink(final UUID contractRuleId) {
        final var baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString();
        final var path = RuleController.class
                .getAnnotation(RequestMapping.class).value()[0];
        return baseUrl + path + "/" + contractRuleId;
    }

    private String getContractRuleContractsLink(final UUID contractRuleId) {
        return WebMvcLinkBuilder.linkTo(methodOn(RulesToContractsController.class)
                .getResource(contractRuleId, null, null)).toString();
    }
}
