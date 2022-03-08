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

import io.dataspaceconnector.controller.resource.view.util.SelfLinkHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = SelfLinkHelper.class)
public class SelfLinkHelperValueTest {

    @Autowired
    private SelfLinkHelper utilSelfLinkHelper;

    @Test
    void createComponent_fillValueField() {
        /* ASSERT */
        final var baseUrl = (String) ReflectionTestUtils.getField(utilSelfLinkHelper, "baseUrl");
        assertNotNull(baseUrl);
    }

}
