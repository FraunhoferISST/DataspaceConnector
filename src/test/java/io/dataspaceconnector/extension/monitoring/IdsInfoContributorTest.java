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
package io.dataspaceconnector.extension.monitoring;

import de.fraunhofer.ids.messaging.core.daps.TokenProviderService;
import io.dataspaceconnector.common.ids.ConnectorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { IdsInfoContributor.class })
class IdsInfoContributorTest {

    @MockBean
    ConnectorService connectorSvc;

    @MockBean
    TokenProviderService tokenProvSvc;

    @Autowired
    private IdsInfoContributor idsInfoContributor;

    @Test
    @WithMockUser("ADMIN")
    public void contribute_validInformation_equals() {
        /* ARRANGE */
        var builder = new Info.Builder();

        /* ACT */
        idsInfoContributor.contribute(builder);
        var info = builder.build();

        /* ASSERT */
        assertNotNull(info.get("configuration"));
        assertNotNull(info.get("ids"));
    }
}
