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

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = { CamelInfoContributorExt.class })
class CamelInfoContributorExtTest {

    @Autowired
    private CamelInfoContributorExt camelInfoContributor;

    @MockBean
    private CamelContext camelContext;

    @Test
    @WithMockUser("ADMIN")
    public void contribute_validContextInformation_equals() {
        /* ARRANGE */
        final var date = new Date();
        final var status = ServiceStatus.Started;
        Map<String, Object> map = Map.of(
                "name", "camel-1",
                "version", "3.11.2",
                "startDate", date,
                "uptime", "6s783ms",
                "status", status
        );

        Mockito.doReturn("camel-1").when(camelContext).getName();
        Mockito.doReturn("3.11.2").when(camelContext).getVersion();
        Mockito.doReturn(date).when(camelContext).getStartDate();
        Mockito.doReturn("6s783ms").when(camelContext).getUptime();
        Mockito.doReturn(status).when(camelContext).getStatus();

        var builder = new Info.Builder();
        camelInfoContributor.contribute(builder);

        /* ACT */
        camelInfoContributor.contribute(builder);
        var info = builder.build();

        /* ASSERT */
        final var resultMap = (Map<String, Object>) info.get("camel");
        assertEquals(map.get("name"), resultMap.get("name"));
        assertEquals(map.get("version"), resultMap.get("version"));
        assertEquals(map.get("startDate"), resultMap.get("startDate"));
        assertEquals(map.get("uptime"), resultMap.get("uptime"));
        assertEquals(map.get("status").toString(), resultMap.get("status").toString());
    }
}
