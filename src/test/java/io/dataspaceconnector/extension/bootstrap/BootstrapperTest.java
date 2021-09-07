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
package io.dataspaceconnector.extension.bootstrap;

import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessageBuilder;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.ids.messaging.requests.MessageContainer;
import io.dataspaceconnector.common.util.Utils;
import io.dataspaceconnector.model.catalog.Catalog;
import io.dataspaceconnector.model.resource.OfferedResource;
import io.dataspaceconnector.model.template.CatalogTemplate;
import io.dataspaceconnector.service.message.GlobalMessageService;
import io.dataspaceconnector.service.resource.templatebuilder.CatalogTemplateBuilder;
import io.dataspaceconnector.service.resource.type.CatalogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BootstrapperTest {

    @MockBean
    GlobalMessageService messageService;

    @MockBean
    CatalogService catalogService;

    @MockBean
    CatalogTemplateBuilder templateBuilder;

    @Autowired
    Bootstrapper bootstrapper;

    private UUID catalogOneId = UUID.fromString("550e8400-e29b-11d4-a716-446655440000");
    private UUID catalogTwoId = UUID.fromString("a1ed9763-e8c4-441b-bd94-d06996fced9e");
    private Catalog catalogOne = getCatalogOne();
    private Catalog catalogTwo = getCatalogTwo();
    private boolean toggle = false;
    private List<Catalog> catalogList = new ArrayList<>();

    private Catalog createCatalog(final CatalogTemplate template) {
        if (toggle) {
            toggle = false;
            catalogList.add(catalogOne);
            return catalogOne;
        } else {
            catalogList.add(catalogTwo);
            return catalogTwo;
        }
    }

    @SneakyThrows
    @Test
    public void bootstrap_files_registerCatalogs() {
        /* ARRANGE */
        Mockito.doReturn(getResponse()).when(messageService).sendConnectorUpdateMessage(Mockito.any());

        Mockito.doAnswer(x -> Utils.toPage(catalogList, Pageable.unpaged()))
               .when(catalogService)
               .getAll(Mockito.any());

        Mockito.doAnswer(x -> createCatalog(x.getArgument(0)))
               .when(templateBuilder)
               .build(Mockito.any(CatalogTemplate.class));

        Mockito.doReturn(catalogOne).when(catalogService).get(Mockito.eq(catalogOneId));
        Mockito.doReturn(catalogTwo).when(catalogService).get(Mockito.eq(catalogTwoId));


        /* ACT */
        bootstrapper.bootstrap();

        /* ASSERT */
        assertEquals(2, catalogService.getAll(Pageable.unpaged()).getSize());
    }


    @SneakyThrows
    private Catalog getCatalogOne() {
        final var constructor = Catalog.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", catalogOneId);
        ReflectionTestUtils.setField(output, "offeredResources", new ArrayList<OfferedResource>());
        ReflectionTestUtils.setField(output, "additional", new HashMap<String, String>());
        return output;
    }

    @SneakyThrows
    private Catalog getCatalogTwo() {
        final var constructor = Catalog.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        final var output = constructor.newInstance();
        ReflectionTestUtils.setField(output, "id", catalogTwoId);
        ReflectionTestUtils.setField(output, "additional", new HashMap<String, String>());
        return output;
    }

    @SneakyThrows
    private Optional<MessageContainer<?>> getResponse() {
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        final var connectorId = URI.create("https://connector");
        final var modelVersion = "4.0.0";
        final var token = new DynamicAttributeTokenBuilder()
                ._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build();
        final var message = new MessageProcessedNotificationMessageBuilder()
                ._securityToken_(token)
                ._modelVersion_(modelVersion)
                ._issuerConnector_(connectorId)
                ._correlationMessage_(URI.create("https://message"))
                ._senderAgent_(connectorId)
                ._issued_(xmlCalendar)
                .build();
        return Optional.of(new MessageContainer<>(message, "EMPTY"));
    }
}
