package de.fraunhofer.isst.dataspaceconnector.repositories;

import de.fraunhofer.isst.dataspaceconnector.model.ContractRule;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class RuleRepositoryIT {

    @Autowired
    private RuleRepository repository;

    @Test
    public void rule_can_hold_min_1_gb() {
        /* ARRANGE */
        final var rule = get1GBRule();

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> repository.saveAndFlush(rule));
    }

    // NOTE This needs to be tested with the postgres db.

    @SneakyThrows
    private ContractRule get1GBRule() {
        /* ARRANGE */
        final var builder = new StringBuilder();

        // Java chars are 2 bytes big (ref https://stackoverflow.com/questions/2474486/create-a-java-variable-string-of-a-specific-size-mbs)
        // For 1GB big array (ref https://www.postgresql.org/docs/12/limits.html)
        // 1 GB = 1073741824 Bytes -> We need 536870912 Chars

        for(long i = 0L; i < 536870912; i++)
            builder.append("0");

        final var constructor = ContractRule.class.getConstructor();
        constructor.setAccessible(true);
        final var rule = constructor.newInstance();

        final var valueField = rule.getClass().getDeclaredField("value");
        valueField.setAccessible(true);
        valueField.set(rule, builder.toString());

        return rule;
    }
}
