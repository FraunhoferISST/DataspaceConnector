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

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;

/**
 * Configures Freemarker, which is used for generating Camel XML routes.
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {

    /**
     * Creates and returns the Freemarker configuration.
     *
     * @return the Freemarker configuration.
     */
    @Bean
    public Configuration freemarkerConfiguration() {
        return new Configuration(Configuration.VERSION_2_3_31) {{
            setClassForTemplateLoading(this.getClass(), "/camel-templates");
            setDefaultEncoding("UTF-8");
            setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            setLogTemplateExceptions(false);
            setWrapUncheckedExceptions(true);
            setFallbackOnNullLoopVariable(false);
        }};
    }

}
