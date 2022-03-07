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
package io.dataspaceconnector.service.routing;

import java.util.UUID;

import io.dataspaceconnector.config.camel.FreemarkerConfig;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.datasource.DatabaseDataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.xml.sax.InputSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {FreemarkerConfig.class, BeanManager.class})
class BeanManagerTest {

    @MockBean
    private XmlBeanDefinitionReader beanReader;

    @MockBean
    private BeanDefinitionRegistry beanRegistry;

    @Autowired
    private BeanManager beanManager;

    @Test
    @SneakyThrows
    void createDataSourceBean_validInput_createBean() {
        /* ARRANGE */
        final var dataSource = getDataSource();

        when(beanReader.loadBeanDefinitions(any(InputSource.class))).thenReturn(1);

        /* ACT */
        beanManager.createDataSourceBean(dataSource);

        /* ASSERT */
        verify(beanReader, times(1)).loadBeanDefinitions(any(InputSource.class));
    }

    @Test
    void removeDataSourceBean_beanPresent_removeBean() {
        /* ARRANGE */
        final var id = UUID.randomUUID();
        doNothing().when(beanRegistry).removeBeanDefinition(any());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> beanManager.removeDataSourceBean(id));
    }

    @Test
    void removeDataSourceBean_beanNotPresent_doNothing() {
        /* ARRANGE */
        final var id = UUID.randomUUID();
        doThrow(NoSuchBeanDefinitionException.class).when(beanRegistry).removeBeanDefinition(any());

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> beanManager.removeDataSourceBean(id));
    }

    private DatabaseDataSource getDataSource() {
        final var dataSource = new DatabaseDataSource();
        ReflectionTestUtils.setField(dataSource, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(dataSource, "url", "someUrl");
        ReflectionTestUtils.setField(dataSource, "driverClassName", "someDriver");

        final var auth = new BasicAuth("username", "password");
        ReflectionTestUtils.setField(dataSource, "authentication", auth);

        return dataSource;
    }

}
