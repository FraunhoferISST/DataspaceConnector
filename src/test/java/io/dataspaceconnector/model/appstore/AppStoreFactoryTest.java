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
package io.dataspaceconnector.model.appstore;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppStoreFactoryTest {

    final AppStoreDesc desc = new AppStoreDesc();
    final AppStoreFactory factory = new AppStoreFactory();

    @Test
    void create_validDesc_returnNew() {
        /* ARRANGE */
        final var title = "MyAppStore";
        desc.setTitle(title);

        /* ACT */
        final var result = factory.create(desc);

        /* ASSERT */
        assertEquals(title, result.getTitle());
        assertTrue(result.getApps().isEmpty());
    }

    @Test
    void update_newLocation_willUpdate() {
        /* ARRANGE */
        final var desc = new AppStoreDesc();
        desc.setLocation(URI.create("https://someLocation"));
        final var appstore = factory.create(new AppStoreDesc());

        /* ACT */
        final var result = factory.update(appstore, desc);

        /* ASSERT */
        assertTrue(result);
        assertEquals(desc.getLocation(), appstore.getLocation());
    }

    @Test
    void update_sameLocation_willNotUpdate() {
        /* ARRANGE */
        final var desc = new AppStoreDesc();
        final var appStore = factory.create(new AppStoreDesc());

        /* ACT */
        final var result = factory.update(appStore, desc);

        /* ASSERT */
        assertFalse(result);
        assertEquals(AppStoreFactory.DEFAULT_URI, appStore.getLocation());
    }
}
