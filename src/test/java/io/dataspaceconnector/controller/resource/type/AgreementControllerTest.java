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
package io.dataspaceconnector.controller.resource.type;

import io.dataspaceconnector.controller.resource.base.exception.MethodNotAllowed;
import io.dataspaceconnector.controller.resource.view.agreement.AgreementViewAssembler;
import io.dataspaceconnector.model.agreement.Agreement;
import io.dataspaceconnector.model.agreement.AgreementDesc;
import io.dataspaceconnector.service.resource.type.AgreementService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.PagedResourcesAssembler;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {AgreementController.class})
class AgreementControllerTest {
    @MockBean
    private AgreementService service;

    @MockBean
    private AgreementViewAssembler assembler;

    @MockBean
    private PagedResourcesAssembler<Agreement> pagedAssembler;

    @Autowired
    @InjectMocks
    private AgreementController controller;

    /**
     * create.
     */

    @Test
    public void create_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.create(null));
    }

    @Test
    public void create_validDesc_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.create(new AgreementDesc()));
    }

    /**
     * update.
     */
    @Test
    public void update_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.update(null, null));
    }

    @Test
    public void update_validInput_returnMethodNotAllowed() {
        /* ARRANGE */
        final var id = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.update(id, new AgreementDesc()));
    }

    /**
     * delete.
     */
    @Test
    public void delete_null_returnMethodNotAllowed() {
        /* ARRANGE */
        // Nothing to arrange.

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.delete(null));
    }

    @Test
    public void delete_anyId_returnMethodNotAllowed() {
        /* ARRANGE */
        final var id = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");

        /* ACT && ASSERT */
        assertThrows(MethodNotAllowed.class, () -> controller.delete(id));
    }
}
