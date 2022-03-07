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
package io.dataspaceconnector.extension.monitoring.update;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { UpdateInfoContributor.class })
public class UpdateInfoContributorTest {

    @Autowired
    private UpdateInfoContributor updateInfoContributor;

    @MockBean
    private UpdateInfoService updateInfoService;

    @Test
    @WithMockUser("ADMIN")
    @SneakyThrows
    public void contribute_validInput_equals() {
        /* ARRANGE */
        var builder = new Info.Builder();
        updateInfoContributor.contribute(builder);
        Map<String, Object> updateMap = Map.of(
                "update", "6.2.0",
                "version", "6.2.0"
        );
        Mockito.when(updateInfoService.getUpdateDetails()).thenReturn(updateMap);

        /* ACT */
        updateInfoContributor.contribute(builder);
        var info = builder.build();

        /* ASSERT */
        assertEquals(updateMap, info.get("update"));
    }

    @Test
    @WithMockUser("ADMIN")
    @SneakyThrows
    public void contribute_thrownException_catchException() {
        /* ARRANGE */
        var builder = new Info.Builder();
        updateInfoContributor.contribute(builder);
        Mockito.doThrow(new IOException()).when(updateInfoService).getUpdateDetails();

        /* ACT */
        updateInfoContributor.contribute(builder);
        var info = builder.build().get("update");

        /* ASSERT */
        assertNotNull(info);
    }
}
