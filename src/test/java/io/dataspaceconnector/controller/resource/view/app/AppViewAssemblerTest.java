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
package io.dataspaceconnector.controller.resource.view.app;

import java.net.URI;

import io.dataspaceconnector.controller.resource.type.AppController;
import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.app.AppDesc;
import io.dataspaceconnector.model.app.AppFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppViewAssemblerTest {

    @Test
    public void create_ValidApp_returnAppView() {
        /* ARRANGE */
        final var shouldLookLike = getApp();
        final var link = new AppViewAssembler().getSelfLink(shouldLookLike.getId(),
                AppController.class);

        /* ACT */
        final var after = getAppView();

        /* ASSERT */
        assertEquals(after.getLicense(), shouldLookLike.getLicense());
        assertEquals(after.getLanguage(), shouldLookLike.getLanguage());
        assertTrue(after.getLinks().contains(link));
    }

    private AppView getAppView() {
        final var assembler = new AppViewAssembler();
        return assembler.toModel(getApp());
    }


    private App getApp() {
        final var factory = new AppFactory();
        return factory.create(getAppDesc());
    }

    private AppDesc getAppDesc() {
        final var desc = new AppDesc();
        desc.setPublisher(URI.create("https://publisher"));
        desc.setTitle("IDS APP");
        desc.setLanguage("DE");
        return desc;
    }

}
