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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.apps.App;
import io.dataspaceconnector.model.apps.AppDesc;
import io.dataspaceconnector.model.apps.AppFactory;
import io.dataspaceconnector.repositories.AppRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {AppService.class})
public class AppServiceTest {

    @MockBean
    private AppRepository appRepository;

    @MockBean
    private AppFactory appFactory;

    @Autowired
    @InjectMocks
    private AppService appService;

    AppDesc appDesc = getAppDesc();
    App app = getApp();

    UUID validId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");

    /**
     * SETUP
     */
    @BeforeEach
    public void init() {
        Mockito.when(appFactory.create(any())).thenReturn(app);
        Mockito.when(appRepository.saveAndFlush(Mockito.eq(app)))
                .thenReturn(app);
        Mockito.when(appRepository.findById(Mockito.eq(app.getId())))
                .thenReturn(Optional.of(app));
    }

    /**********************************************************************
     * GET
     **********************************************************************/
    @Test
    public void get_nullId_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appService.get(null));
    }

    @Test
    public void get_knownId_returnApp() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = appService.get(app.getId());

        /* ASSERT */
        assertEquals(app.getId(), result.getId());
        assertEquals(app.getTitle(), result.getTitle());
    }

    /**********************************************************************
     * CREATE
     **********************************************************************/
    @Test
    public void create_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appService.create(null));
    }

    @Test
    public void create_ValidDesc_returnHasId() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var newApp = appService.create(appDesc);

        /* ASSERT */
        assertEquals(app, newApp);
    }

    /**********************************************************************
     * UPDATE
     **********************************************************************/
    @Test
    public void update_nullDesc_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> appService.update(app.getId(), null));
    }

    @Test
    public void update_NewDesc_returnUpdatedEntity() {
        /* ARRANGE */
        final var shouldLookLike = getAppFromValidDesc(validId,
                getNewApp(getUpdatedAppDesc()));

        /* ACT */
        final var after =
                appService.update(validId, getUpdatedAppDesc());

        /* ASSERT */
        assertEquals(after, shouldLookLike);
    }

    /**********************************************************************
     * DELETE
     **********************************************************************/
    @Test
    public void delete_nullId_throwsIllegalArgumentException() {
        /* ARRANGE */

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> appService.delete(null));
    }

    /**********************************************************************
     * UTILITIES
     **********************************************************************/
    @SneakyThrows
    private App getApp() {
        final var desc = getAppDesc();

        final var appConstructor = App.class.getConstructor();

        final var app = appConstructor.newInstance();

        final var idField = app.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(app, UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e"));

        final var titlelField = app.getClass().getDeclaredField("title");
        titlelField.setAccessible(true);
        titlelField.set(app, desc.getTitle());

        return app;
    }

    private App getNewApp(final AppDesc updatedAppDesc) {
        return appFactory.create(updatedAppDesc);
    }

    @SneakyThrows
    private App getAppFromValidDesc(final UUID id, final App app) {
        final var idField = app.getClass().getSuperclass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(app, id);

        return app;
    }

    private AppDesc getAppDesc() {
        final var desc = new AppDesc();
        desc.setTitle("App");

        return desc;
    }

    private AppDesc getUpdatedAppDesc() {
        final var desc = new AppDesc();
        desc.setTitle("New App");

        return desc;
    }
}
