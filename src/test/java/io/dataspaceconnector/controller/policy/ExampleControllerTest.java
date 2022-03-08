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
package io.dataspaceconnector.controller.policy;

import io.dataspaceconnector.model.pattern.ConnectorRestrictionDesc;
import io.dataspaceconnector.model.pattern.DeletionDesc;
import io.dataspaceconnector.model.pattern.DurationDesc;
import io.dataspaceconnector.model.pattern.IntervalDesc;
import io.dataspaceconnector.model.pattern.LoggingDesc;
import io.dataspaceconnector.model.pattern.NotificationDesc;
import io.dataspaceconnector.model.pattern.PermissionDesc;
import io.dataspaceconnector.model.pattern.ProhibitionDesc;
import io.dataspaceconnector.model.pattern.SecurityRestrictionDesc;
import io.dataspaceconnector.model.pattern.UsageNumberDesc;
import io.dataspaceconnector.common.ids.DeserializationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {ExampleController.class})
public class ExampleControllerTest {

    @MockBean
    private DeserializationService deserializationService;

    @Autowired
    private ExampleController controller;

    @Test
    public void getExampleUsagePolicy_permissionDesc_IsBuild() {
        final var result = controller.getExampleUsagePolicy(new PermissionDesc());
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_prohibitionDesc_isBuild() {
        final var result = controller.getExampleUsagePolicy(new ProhibitionDesc());
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_usageNumberDesc_isBuild() {
        final var desc = new UsageNumberDesc();
        desc.setValue("5");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_durationDesc_isBuild() {
        final var desc = new DurationDesc();
        desc.setValue("PT1M30.5S");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_intervalDesc_isBuild() {
        final var desc = new IntervalDesc();
        desc.setStart("2020-07-11T00:00:00Z");
        desc.setEnd("2020-07-11T00:00:00Z");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_deletionDesc_isBuild() {
        final var desc = new DeletionDesc();
        desc.setStart("2020-07-11T00:00:00Z");
        desc.setEnd("2020-07-11T00:00:00Z");
        desc.setDate("2020-07-11T00:00:00Z");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_loggingDesc_isBuild() {
        final var result = controller.getExampleUsagePolicy(new LoggingDesc());
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_notificationDesc_isBuild() {
        final var desc = new NotificationDesc();
        desc.setUrl("https://localhost:8080/api/ids/data");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_connectorRestrictedDesc_isBuild() {
        final var desc = new ConnectorRestrictionDesc();
        desc.setUrl("https://localhost:8080");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }

    @Test
    public void getExampleUsagePolicy_securityRestrictedDesc_isBuild() {
        final var desc = new SecurityRestrictionDesc();
        desc.setProfile("BASE_SECURITY_PROFILE");
        final var result = controller.getExampleUsagePolicy(desc);
        Assertions.assertNotNull(result);
    }
}
