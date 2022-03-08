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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.rule.ContractRule;
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
