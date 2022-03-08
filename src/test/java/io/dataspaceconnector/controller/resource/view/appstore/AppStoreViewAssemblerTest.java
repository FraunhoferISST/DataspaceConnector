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
package io.dataspaceconnector.controller.resource.view.appstore;

import java.net.URI;

import io.dataspaceconnector.controller.resource.type.AppStoreController;
import io.dataspaceconnector.model.appstore.AppStore;
import io.dataspaceconnector.model.appstore.AppStoreDesc;
import io.dataspaceconnector.model.appstore.AppStoreFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppStoreViewAssemblerTest {

    @Test
    public void create_ValidAppStore_returnAppStoreView() {
        /* ARRANGE */
        final var shouldLookLike = getAppStore();
        final var link = new AppStoreViewAssembler()
                .getSelfLink(shouldLookLike.getId(), AppStoreController.class);

        /* ACT */
        final var after = getAppStoreView();

        /* ASSERT */
        assertEquals(after.getLocation(), shouldLookLike.getLocation());
        assertEquals(after.getTitle(), shouldLookLike.getTitle());
        assertTrue(after.getLinks().contains(link));
    }

    private AppStoreView getAppStoreView() {
        final var assembler = new AppStoreViewAssembler();
        return assembler.toModel(getAppStore());
    }


    private AppStore getAppStore() {
        final var factory = new AppStoreFactory();
        return factory.create(getAppStoreDesc());
    }

    private AppStoreDesc getAppStoreDesc() {
        final var desc = new AppStoreDesc();
        desc.setLocation(URI.create("https://example.org"));
        desc.setTitle("Example AppStore");
        return desc;
    }
}
