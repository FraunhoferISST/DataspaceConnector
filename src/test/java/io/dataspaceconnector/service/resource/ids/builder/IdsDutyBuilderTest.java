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
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.LeftOperand;
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

@SpringBootTest(classes = {ContractRuleFactory.class, IdsDutyBuilder.class,
        DeserializationService.class, SerializerProvider.class})
public class IdsDutyBuilderTest {

    @Autowired
    private ContractRuleFactory contractRuleFactory;

    @Autowired
    private IdsDutyBuilder idsDutyBuilder;

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
        assertThrows(NullPointerException.class, () -> idsDutyBuilder.create(null));
    }

    @Test
    public void create_ruleWithoutId_returnRuleWithNewId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithoutId());

        /* ACT */
        final var idsRule = idsDutyBuilder.create(rule);

        /* ASSERT */
        assertEquals(DutyImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.DELETE, idsRule.getAction().get(0));

        assertEquals(1, idsRule.getConstraint().size());
        Constraint constraint = (Constraint) idsRule.getConstraint().get(0);
        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType());
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertTrue(idsRule.getDescription().isEmpty());
    }

    @Test
    public void create_ruleWithId_returnRuleWithReplacedId() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithId());

        /* ACT */
        final var idsRule = idsDutyBuilder.create(rule);

        /* ASSERT */
        assertEquals(DutyImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertEquals(1, idsRule.getAction().size());
        assertEquals(Action.DELETE, idsRule.getAction().get(0));

        assertEquals(1, idsRule.getConstraint().size());
        Constraint constraint = (Constraint) idsRule.getConstraint().get(0);
        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType());
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertTrue(idsRule.getDescription().isEmpty());
    }

    @Test
    public void create_invalidRuleJson_throwIllegalArgumentException() {
        /* ARRANGE */
        final var json = "{\"not\": \"a rule\"}";
        final var rule = getContractRule(json);

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsDutyBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithInvalidType_throwIllegalArgumentException() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithInvalidType());

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> idsDutyBuilder.create(rule));
    }

    @Test
    public void create_ruleJsonWithMissingAction_returnRuleWithMissingAction() {
        /* ARRANGE */
        final var rule = getContractRule(getRuleWithMissingAction());

        /* ACT */
        final var idsRule = idsDutyBuilder.create(rule);

        /* ASSERT */
        assertEquals(DutyImpl.class, idsRule.getClass());
        assertTrue(idsRule.getId().isAbsolute());
        assertTrue(idsRule.getId().toString().contains(rule.getId().toString()));

        assertTrue(idsRule.getAction().isEmpty());

        assertEquals(1, idsRule.getConstraint().size());
        Constraint constraint = (Constraint) idsRule.getConstraint().get(0);
        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType());
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertTrue(idsRule.getDescription().isEmpty());
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
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
                + "-499eec24bc57\",\n"
                + "      \"ids:rightOperand\" : {\n"
                + "        \"@value\" : \"2020-07-11T00:00:00Z\",\n"
                + "        \"@type\" : \"xsd:dateTimeStamp\"\n"
                + "      },\n"
                + "      \"ids:operator\" : {\n"
                + "        \"@id\" : \"idsc:TEMPORAL_EQUALS\"\n"
                + "      },\n"
                + "      \"ids:leftOperand\" : {\n"
                + "        \"@id\" : \"idsc:POLICY_EVALUATION_TIME\"\n"
                + "      }\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithoutId() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
                + "-499eec24bc57\",\n"
                + "      \"ids:rightOperand\" : {\n"
                + "        \"@value\" : \"2020-07-11T00:00:00Z\",\n"
                + "        \"@type\" : \"xsd:dateTimeStamp\"\n"
                + "      },\n"
                + "      \"ids:operator\" : {\n"
                + "        \"@id\" : \"idsc:TEMPORAL_EQUALS\"\n"
                + "      },\n"
                + "      \"ids:leftOperand\" : {\n"
                + "        \"@id\" : \"idsc:POLICY_EVALUATION_TIME\"\n"
                + "      }\n"
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
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id" +
                ".org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
                + "-499eec24bc57\",\n"
                + "      \"ids:rightOperand\" : {\n"
                + "        \"@value\" : \"2020-07-11T00:00:00Z\",\n"
                + "        \"@type\" : \"xsd:dateTimeStamp\"\n"
                + "      },\n"
                + "      \"ids:operator\" : {\n"
                + "        \"@id\" : \"idsc:TEMPORAL_EQUALS\"\n"
                + "      },\n"
                + "      \"ids:leftOperand\" : {\n"
                + "        \"@id\" : \"idsc:POLICY_EVALUATION_TIME\"\n"
                + "      }\n"
                + "    } ]\n"
                + "  }";
    }

    private String getRuleWithMissingAction() {
        return "{\n"
                + "   \"@context\" : {\n"
                + "      \"ids\" : \"https://w3id.org/idsa/core/\",\n"
                + "      \"idsc\" : \"https://w3id.org/idsa/code/\"\n"
                + "      },"
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:constraint\" : [ {\n"
                + "      \"@type\" : \"ids:Constraint\",\n"
                + "      \"@id\" : \"https://w3id.org/idsa/autogen/constraint/f2acf67f-bc4c-4e64" +
                "-87fc"
                + "-499eec24bc57\",\n"
                + "      \"ids:rightOperand\" : {\n"
                + "        \"@value\" : \"2020-07-11T00:00:00Z\",\n"
                + "        \"@type\" : \"xsd:dateTimeStamp\"\n"
                + "      },\n"
                + "      \"ids:operator\" : {\n"
                + "        \"@id\" : \"idsc:TEMPORAL_EQUALS\"\n"
                + "      },\n"
                + "      \"ids:leftOperand\" : {\n"
                + "        \"@id\" : \"idsc:POLICY_EVALUATION_TIME\"\n"
                + "      }\n"
                + "    } ]\n"
                + "  }";
    }

}
