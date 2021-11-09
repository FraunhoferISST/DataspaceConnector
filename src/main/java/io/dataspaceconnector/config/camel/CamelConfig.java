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
package io.dataspaceconnector.config.camel;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.model.Constants;
import org.apache.camel.processor.errorhandler.RedeliveryPolicy;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Contains beans required for reading, deploying, and/or removing routes and beans as well as
 * error handling in routes.
 */
@Configuration
public class CamelConfig {

    /**
     * Creates an Unmarshaller bean that can be used to read bean definitions from XML.
     *
     * @return the Unmarshaller.
     */
    @Bean
    public Unmarshaller unmarshaller() {
        try {
            return JAXBContext.newInstance(Constants.JAXB_CONTEXT_PACKAGES).createUnmarshaller();
        } catch (JAXBException e) {
            throw new BeanCreationException("Failed to create Unmarshaller.", e);
        }
    }

    /**
     * Creates and configures an XmlBeanDefinitionReader bean that reads beans from XML and
     * automatically adds them to the application context.
     *
     * @param appContext the application context.
     * @return the XmlBeanDefinitionReader.
     */
    @Bean
    public XmlBeanDefinitionReader xmlBeanDefinitionReader(final GenericApplicationContext
                                                                   appContext) {
        final var xmlBeanReader = new XmlBeanDefinitionReader(appContext);
        xmlBeanReader.setValidationMode(XmlBeanDefinitionReader.VALIDATION_XSD);
        return xmlBeanReader;
    }

    /**
     * Creates the application context's BeanDefinitionRegistry as a bean that can be used to
     * remove beans from the context.
     *
     * @param context the application context.
     * @return the BeanDefinitionRegistry.
     */
    @Bean
    public BeanDefinitionRegistry beanDefinitionRegistry(final GenericApplicationContext context) {
        return (BeanDefinitionRegistry) context.getAutowireCapableBeanFactory();
    }

    /**
     * Returns a DeadLetterChannelBuilder instance that routes all failed exchanges to the
     * designated error handler route.
     *
     * @return the DeadLetterChannelBuilder.
     */
    @Bean
    public DeadLetterChannelBuilder errorHandler() {
        final var dlcBuilder = new DeadLetterChannelBuilder();
        dlcBuilder.setDeadLetterUri("direct:deadLetterChannel");
        dlcBuilder.setRedeliveryPolicy(new RedeliveryPolicy().disableRedelivery());
        return dlcBuilder;
    }

}
