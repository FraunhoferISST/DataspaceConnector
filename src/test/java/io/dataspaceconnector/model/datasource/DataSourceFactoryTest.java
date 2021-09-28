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
package io.dataspaceconnector.model.datasource;

import io.dataspaceconnector.common.exception.InvalidEntityException;
import io.dataspaceconnector.model.auth.ApiKey;
import io.dataspaceconnector.model.auth.AuthenticationDesc;
import io.dataspaceconnector.model.auth.BasicAuth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataSourceFactoryTest {

    final DataSourceDesc desc = new DataSourceDesc();
    final DataSourceFactory factory = new DataSourceFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        desc.setType(DataSourceType.DATABASE);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(DataSourceType.DATABASE, result.getType());
    }

    @Test
    void create_typeDatabaseWithApiKey_throwInvalidEntityException() {
        /* ARRANGE */
        desc.setType(DataSourceType.DATABASE);
        desc.setApiKey(new AuthenticationDesc("key", "value"));

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.create(desc));
    }

    @Test
    void update_newBasicAuth_willUpdate() {
        /* ARRANGE */
        final var desc = new DataSourceDesc();
        desc.setBasicAuth(new AuthenticationDesc("", ""));
        final var dataSource = factory.create(new DataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(new BasicAuth(desc.getBasicAuth()), dataSource.getAuthentication());
    }

    @Test
    void update_newApiKeyAuth_willUpdate() {
        /* ARRANGE */
        final var desc = new DataSourceDesc();
        desc.setType(DataSourceType.REST);
        desc.setApiKey(new AuthenticationDesc("", ""));
        final var dataSource = factory.create(new DataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(new ApiKey(desc.getApiKey().getKey(), desc.getApiKey().getValue()),
                dataSource.getAuthentication());
    }

    @Test
    void update_sameAuth_willNotUpdate() {
        /* ARRANGE */
        final var dataSource = factory.create(new DataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, new DataSourceDesc());

        /* ASSERT */
        assertFalse(result);
        assertNull(dataSource.getAuthentication());
    }

    @Test
    void update_newType_willUpdate() {
        /* ARRANGE */
        final var desc = new DataSourceDesc();
        desc.setType(DataSourceType.REST);
        final var dataSource = factory.create(new DataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getType(), dataSource.getType());
    }

    @Test
    void update_sameType_willNotUpdate() {
        /* ARRANGE */
        final var dataSource = factory.create(new DataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, new DataSourceDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(DataSourceFactory.DEFAULT_SOURCE_TYPE, dataSource.getType());
    }

    @Test
    void update_sameAuthHeader_willNotUpdate() {
        /* ARRANGE */
        final var desc = new DataSourceDesc();
        desc.setType(DataSourceType.REST);
        desc.setApiKey(new AuthenticationDesc("key", "value"));
        final var dataSource = factory.create(desc);

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertFalse(result);
    }
}
