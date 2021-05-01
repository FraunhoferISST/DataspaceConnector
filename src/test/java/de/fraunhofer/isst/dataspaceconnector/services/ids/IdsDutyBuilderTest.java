package de.fraunhofer.isst.dataspaceconnector.services.ids;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import de.fraunhofer.iais.eis.Action;
import de.fraunhofer.iais.eis.BinaryOperator;
import de.fraunhofer.iais.eis.Constraint;
import de.fraunhofer.iais.eis.DutyImpl;
import de.fraunhofer.iais.eis.LeftOperand;
import de.fraunhofer.isst.dataspaceconnector.model.AbstractEntity;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleDesc;
import de.fraunhofer.isst.dataspaceconnector.model.ContractRuleFactory;
import de.fraunhofer.isst.dataspaceconnector.services.ids.builder.IdsDutyBuilder;
import de.fraunhofer.isst.ids.framework.configuration.SerializerProvider;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {ContractRuleFactory.class, IdsDutyBuilder.class,
        DeserializationService.class, SerializerProvider.class})
public class IdsDutyBuilderTest {

    @Autowired
    private ContractRuleFactory contractRuleFactory;

    @Autowired
    private IdsDutyBuilder idsDutyBuilder;

    private final ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

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
//        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType()); //TODO always null for xsd:dateTimeStamp
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertNull(idsRule.getDescription());
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
//        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType());
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertNull(idsRule.getDescription());
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

        assertNull(idsRule.getAction());

        assertEquals(1, idsRule.getConstraint().size());
        Constraint constraint = (Constraint) idsRule.getConstraint().get(0);
//        assertEquals("xsd:dateTimeStamp", constraint.getRightOperand().getType());
        assertEquals("2020-07-11T00:00:00Z", constraint.getRightOperand().getValue());
        assertEquals(BinaryOperator.TEMPORAL_EQUALS, constraint.getOperator());
        assertEquals(LeftOperand.POLICY_EVALUATION_TIME, constraint.getLeftOperand());

        assertNull(idsRule.getDescription());
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

        final var idField = AbstractEntity.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(rule, UUID.randomUUID());

        final var creationDateField = AbstractEntity.class.getDeclaredField("creationDate");
        creationDateField.setAccessible(true);
        creationDateField.set(rule, date);

        return rule;
    }

    private String getRuleWithId() {
        return "{\n"
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id.org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
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
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id.org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
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
                + "    \"@type\" : \"ids:Representation\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:action\" : [ {\n"
                + "      \"@id\" : \"idsc:DELETE\"\n"
                + "    } ],\n"
                + "      \"ids:constraint\" : [ {\n"
                + "        \"@type\" : \"ids:Constraint\",\n"
                + "        \"@id\" : \"https://w3id.org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
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
                + "    \"@type\" : \"ids:Duty\",\n"
                + "    \"@id\" : \"https://w3id.org/idsa/autogen/duty/770e6abb-dbe5-4ea3-bff5"
                + "-aa4c29d29fb5\",\n"
                + "    \"ids:constraint\" : [ {\n"
                + "      \"@type\" : \"ids:Constraint\",\n"
                + "      \"@id\" : \"https://w3id.org/idsa/autogen/constraint/f2acf67f-bc4c-4e64-87fc"
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
