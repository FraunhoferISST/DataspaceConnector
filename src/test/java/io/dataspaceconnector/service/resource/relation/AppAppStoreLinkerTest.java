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
package io.dataspaceconnector.service.resource.relation;

import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.service.resource.base.OwningRelationService;
import io.dataspaceconnector.service.resource.type.AppService;
import io.dataspaceconnector.service.resource.type.AppStoreService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {AppAppStoreLinker.class})
class AppAppStoreLinkerTest {

    @MockBean
    AppStoreService appStoreService;

    @MockBean
    AppService appService;

    @MockBean
    OwningRelationService<?, ?, ?, ?> owningService;

    @Autowired
    @InjectMocks
    AppAppStoreLinker linker;

    /***********************************************************************************************
     * getInternal                                                                                 *
     **********************************************************************************************/

    @Test
    public void getInternal_null_throwsNullPointerException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(NullPointerException.class, () -> linker.getInternal(null));
    }

    @Test
    public void getInternal_Valid_returnAppStore() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var appStore = linker.getInternal(getApp());

        /* ASSERT */
        assertEquals(getAppStore().getId(), appStore.get(0).getId());
    }

    /***********************************************************************************************
     * Utilities.                                                                                  *
     **********************************************************************************************/

    @SneakyThrows
    private App getApp() {
        final var constructor = App.class.getConstructor();
        constructor.setAccessible(true);

        final var app = constructor.newInstance();
        ReflectionTestUtils.setField(app, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));
        ReflectionTestUtils.setField(app, "appStore", getAppStore());

        return app;
    }

    @SneakyThrows
    private AppStore getAppStore() {
        final var constructor = AppStore.class.getConstructor();
        constructor.setAccessible(true);

        final var appStore = constructor.newInstance();
        ReflectionTestUtils.setField(appStore, "id", UUID.fromString("554ed409-03e9-4b41-a45a-4b7a8c0aa499"));
        ReflectionTestUtils.setField(appStore, "location", new URI("http://example"));

        return appStore;
    }
}
