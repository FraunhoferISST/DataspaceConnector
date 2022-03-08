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
package io.dataspaceconnector.controller.resource.view;

import java.util.UUID;

import io.dataspaceconnector.controller.resource.type.ArtifactController;
import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SelfLinkHelperTest {

    private SelfLinkHelper helper = new SelfLinkHelper();

    @Test
    public void getSelfLink_bothParametersNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> helper.getSelfLink(null, null));
    }

    @Test
    public void getSelfLink_controllerClassNull_throwIllegalArgumentException() {
        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class,
                () -> helper.getSelfLink(UUID.randomUUID(), null));
    }

    @Test
    public void getSelfLink_entityIdNull_returnBasePathWithoutId() {
        /* ARRANGE */
        final var baseUrl = "https://localhost:8080";
        final var path = ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];

        helper.setBaseUrl(baseUrl);

        /* ACT */
        final var result = helper.getSelfLink(null,
                                                        ArtifactController.class);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path, result.getHref());
        assertEquals("self", result.getRel().value());
    }

    @Test
    public void getSelfLink_inputCorrect_returnSelfLink() {
        /* ARRANGE */
        final var resourceId = UUID.randomUUID();
        final var baseUrl = "https://localhost:8080";
        final var path = ArtifactController.class
                .getAnnotation(RequestMapping.class).value()[0];

        helper.setBaseUrl(baseUrl);

        /* ACT */
        final var result = helper.getSelfLink(resourceId,
                                                        ArtifactController.class);

        /* ASSERT */
        assertNotNull(result);
        assertEquals(baseUrl + path + "/" + resourceId, result.getHref());
        assertEquals("self", result.getRel().value());
    }
}
