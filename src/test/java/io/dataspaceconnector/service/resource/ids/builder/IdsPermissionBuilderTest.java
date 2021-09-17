/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
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
package io.dataspaceconnector.service.resource.ids.builder;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.PermissionImpl;
import de.fraunhofer.ids.messaging.util.SerializerProvider;
import io.dataspaceconnector.common.ids.DeserializationService;
import io.dataspaceconnector.common.net.SelfLinkHelper;
import io.dataspaceconnector.model.base.Entity;
import io.dataspaceconnector.model.rule.ContractRule;
import io.dataspaceconnector.model.rule.ContractRuleDesc;
import io.dataspaceconnector.model.rule.ContractRuleFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ContractRuleFactory.class, IdsPermissionBuilder.class,
        DeserializationService.class, SerializerProvider.class})
public class IdsPermissionBuilderTest {

    @Autowired
    private ContractRuleFactory contractRuleFactory;

    @Autowired
    private IdsPermissionBuilder idsPermissionBuilder;

    @MockBean
    private SelfLinkHelper selfLinkHelper;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    private final UUID uuid = UUID.randomUUID();

    @BeforeEach
    void init() {
        final var uri = URI.create("https://" + uuid);
        when(selfLinkHelper.getSelfLink(any(Entity.class))).thenReturn(uri);
    }

    @Test
    public void create_inputNull_throwNullPointerException() {
        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> idsPermissionBuilder.create(null));
    }

    @Test
    public void create_ruleWithoutId_returnRuleWithNewId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithoutId());

        /* ACT */
        final var idsRule = idsPermissionBuilder.create(rule);

        /* ASSERT */
        assertEquals(PermissionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.USE, idsRule.getAction().get(0));
        assertTrue(idsRule.getConstraint().isEmpty());
        assertEquals(1, idsRule.getDescription().size());
        assertEquals("provide-access", idsRule.getDescription().get(0).getValue());
    }

    @Test
    public void create_ruleWithId_returnRuleWithReplacedId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithId());

        /* ACT */
        final var idsRule = idsPermissionBuilder.create(rule);

        /* ASSERT */
        assertEquals(PermissionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.USE, idsRule.getAction().get(0));
        assertTrue(idsRule.getConstraint().isEmpty());
        assertEquals(1, idsRule.getDescription().size());
        assertEquals("provide-access", idsRule.getDescription().get(0).getValue());
    }

    @Test
    public void create_invalidRuleJson_throwIllegalArgumentException() {
        /* ARRANGE */
        final var json = "{\"not\": \"a rule\"}";
        final var rule = getContractRule(json);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsPermissionBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithInvalidType_throwIllegalArgumentException() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithInvalidType());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsPermissionBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithMissingAction_returnRuleWithMissingAction() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithMissingAction());

        /* ACT */
        final var idsRule = idsPermissionBuilder.create(rule);

        /* ASSERT */
        assertEquals(PermissionImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));
        assertTrue(idsRule.getAction().isEmpty());
    }

    /**************************************************************************
     * Utilities.
     *************************************************************************/

    @SneakyThrows
    private ContractRule getContractRule(final String value) {
        final var ruleDesc = new ContractRuleDesc();
        ruleDesc.setTitle("title");
        ruleDesc.setValue(value);
        final var rule = contractRuleFactory.create(ruleDesc);

        final var idField = Entity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, uuid);

        final var creationDateField = Entity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(rule, date);

        return rule;
    }

    private String getRuleWithId() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Permission\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/ae138d4f-f01d-4358"
                + "-89a7-73e7c560f3de\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithoutId() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Permission\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithInvalidType() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Representation\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/ae138d4f-f01d-4358"
                + "-89a7-73e7c560f3de\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:USE\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithMissingAction() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Permission\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/permission/ae138d4f-f01d-4358"
                + "-89a7-73e7c560f3de\",\n"
                + "    \"ids:description\" : [ {\n"
                + "      \"@value\" : \"provide-access\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ],\n"
                + "    \"ids:title\" : [ {\n"
                + "      \"@value\" : \"Example Usage Policy\",\n"
                + "      \"@type\" : \"http://www.w3.org/2001/XMLSchema#string\"\n"
                + "    } ]\n"
                + "  }";
    }

}
