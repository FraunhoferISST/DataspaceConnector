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
package io.dataspaceconnector.controller.routing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.xml.sax.InputSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {BeansController.class})
public class BeansControllerTest {

    @MockBean
    private XmlBeanDefinitionReader xmlBeanDefinitionReader;

    @MockBean
    private BeanDefinitionRegistry beanDefinitionRegistry;

    @Autowired
    private BeansController beansController;

    @Test
    public void addBeans_fileNull_returnStatusCode400() {
        /* ACT */
        final var response = beansController.addBeans(null);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addBeans_validBeanFile_returnStatusCode200() {
        /* ARRANGE */
        when(xmlBeanDefinitionReader.loadBeanDefinitions(any(InputSource.class))).thenReturn(1);

        final var file = new MockMultipartFile("file", "beans.xml",
                "application/xml",
                getBeanFileContent().getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var response = beansController.addBeans(file);

        /* ASSERT */
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void addBeans_invalidBeanFile_returnStatusCode400() {
        /* ARRANGE */
        when(xmlBeanDefinitionReader.loadBeanDefinitions(any(InputSource.class)))
                .thenThrow(new BeanDefinitionStoreException(""));

        final var file = new MockMultipartFile("file", "beans.xml",
                "application/xml",
                getBeanFileContent().getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var response = beansController.addBeans(file);

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void removeBean_validBeanId_returnStatusCode200() {
        /* ARRANGE */
        doNothing().when(beanDefinitionRegistry).removeBeanDefinition(any());

        /* ACT */
        final var response = beansController.removeBean("validId");

        /* ASSERT */
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void removeBean_invalidBeanId_returnStatusCode400() {
        /* ARRANGE */
        doThrow(NoSuchBeanDefinitionException.class)
                .when(beanDefinitionRegistry).removeBeanDefinition(any());

        /* ACT */
        final var response = beansController.removeBean("invalidId");

        /* ASSERT */
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private String getBeanFileContent() {
        return "<bean id=\"bean-id\" class=\"bean-class\"></bean>";
    }

}
