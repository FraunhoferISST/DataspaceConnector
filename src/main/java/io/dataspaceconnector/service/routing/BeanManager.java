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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import io.dataspaceconnector.model.auth.BasicAuth;
import io.dataspaceconnector.model.datasource.DatabaseDataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

import static io.dataspaceconnector.common.util.Utils.escapeForXml;

/**
 * Component for creating Spring beans required in Camel routes.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class BeanManager {

    /**
     * The Freemarker configuration.
     */
    private final @NonNull Configuration freemarkerConfig;

    /**
     * Reader for parsing beans from XML and automatically adding them to application context.
     */
    private final @NonNull XmlBeanDefinitionReader beanReader;

    /**
     * Bean registry of the application context.
     */
    private final @NonNull BeanDefinitionRegistry beanRegistry;

    /**
     * Creates a data source bean for a {@link DatabaseDataSource}.
     *
     * @param dataSource input containing all required parameters for creating the bean.
     * @throws BeanCreationException if creating the bean fails.
     */
    @SuppressFBWarnings("TEMPLATE_INJECTION_FREEMARKER")
    public void createDataSourceBean(final DatabaseDataSource dataSource)
            throws BeanCreationException {
        final var freemarkerInput = new HashMap<String, Object>();

        freemarkerInput.put("dataSourceId", dataSource.getId());
        freemarkerInput.put("url", escapeForXml(dataSource.getUrl()));
        freemarkerInput.put("driver", dataSource.getDriverClassName());

        final var auth = (BasicAuth) dataSource.getAuthentication();
        freemarkerInput.put("username", auth.getUsername());
        freemarkerInput.put("password", auth.getPassword());

        try {
            final var template = freemarkerConfig.getTemplate("datasource_bean_template.ftl");
            final var writer = new StringWriter();
            template.process(freemarkerInput, writer);
            final var xml = writer.toString();
            beanReader.loadBeanDefinitions(new InputSource(new StringReader(xml)));
        } catch (IOException | TemplateException e) {
            if (log.isWarnEnabled()) {
                log.warn("Failed to create data source bean. [exception=({})]", e.getMessage());
            }

            throw new BeanCreationException("Failed to create data source bean.", e);
        }
    }

    /**
     * Deletes a data source bean corresponding to a {@link DatabaseDataSource}.
     *
     * @param id ID of the {@link io.dataspaceconnector.model.datasource.DataSource} for which the
     *           bean should be deleted.
     */
    public void removeDataSourceBean(final UUID id) {
        try {
            beanRegistry.removeBeanDefinition(id.toString());
        } catch (NoSuchBeanDefinitionException exception) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to delete bean because it does not exist. [id=({})]", id);
            }
            // No further action required.
        }
    }
}
