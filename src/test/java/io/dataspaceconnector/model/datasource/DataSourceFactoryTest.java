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

    final DataSourceDesc desc = new RestDataSourceDesc();
    final DataSourceFactory factory = new DataSourceFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(DataSourceType.REST, result.getType());
    }

    @Test
    void create_typeDatabaseWithApiKey_throwInvalidEntityException() {
        /* ARRANGE */
        final var databaseDesc = new DatabaseDataSourceDesc();
        databaseDesc.setApiKey(new AuthenticationDesc("key", "value"));

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.create(databaseDesc));
    }

    @Test
    void update_newBasicAuth_willUpdate() {
        /* ARRANGE */
        final var desc = new RestDataSourceDesc();
        desc.setBasicAuth(new AuthenticationDesc("", ""));
        final var dataSource = factory.create(new RestDataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(new BasicAuth(desc.getBasicAuth()), dataSource.getAuthentication());
    }

    @Test
    void update_newApiKeyAuth_willUpdate() {
        /* ARRANGE */
        final var desc = new RestDataSourceDesc();
        desc.setApiKey(new AuthenticationDesc("", ""));
        final var dataSource = factory.create(new RestDataSourceDesc());

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
        final var dataSource = factory.create(new RestDataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, new RestDataSourceDesc());

        /* ASSERT */
        assertFalse(result);
        assertNull(dataSource.getAuthentication());
    }

    @Test
    void update_setAuthenticationNull_willUpdate() {
        /* ARRANGE */
        final var desc = new RestDataSourceDesc();
        desc.setBasicAuth(new AuthenticationDesc("key", "value"));
        final var dataSource = factory.create(desc);

        /* ACT */
        final var result = factory.update(dataSource, new RestDataSourceDesc());

        /* ASSERT */
        assertTrue(result);
        assertNull(dataSource.getAuthentication());
    }

    @Test
    void update_newType_throwException() {
        /* ARRANGE */
        final var desc = new RestDataSourceDesc();
        final var newDesc = new DatabaseDataSourceDesc();
        newDesc.setUrl("https://someUrl");
        newDesc.setDriverClassName("some.driver.class");
        final var dataSource = factory.create(newDesc);

        /* ACT && ASSERT */
        assertThrows(InvalidEntityException.class, () -> factory.update(dataSource, desc));
    }

    @Test
    void update_sameType_willNotUpdate() {
        /* ARRANGE */
        final var dataSource = factory.create(new RestDataSourceDesc());

        /* ACT */
        final var result = factory.update(dataSource, new RestDataSourceDesc());

        /* ASSERT */
        assertFalse(result);
        assertEquals(DataSourceType.REST, dataSource.getType());
    }

    @Test
    void update_sameAuthHeader_willNotUpdate() {
        /* ARRANGE */
        final var desc = new RestDataSourceDesc();
        desc.setApiKey(new AuthenticationDesc("key", "value"));
        final var dataSource = factory.create(desc);

        /* ACT */
        final var result = factory.update(dataSource, desc);

        /* ASSERT */
        assertFalse(result);
    }
}
