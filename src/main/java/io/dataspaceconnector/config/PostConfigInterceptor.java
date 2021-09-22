///*
// * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.dataspaceconnector.config;
//
//import de.fraunhofer.ids.messaging.core.config.ConfigContainer;
//import de.fraunhofer.ids.messaging.core.config.ConfigProducerInterceptorException;
//import de.fraunhofer.ids.messaging.core.config.ConfigUpdateException;
//import de.fraunhofer.ids.messaging.core.config.PostConfigProducerInterceptor;
//import io.dataspaceconnector.common.ids.mapping.FromIdsObjectMapper;
//import io.dataspaceconnector.service.resource.type.ConfigurationService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * Interceptor, saving parsed configuration into db. Only present, if {@link PreConfigInterceptor}
// * is disabled.
// */
//@Slf4j
//@AllArgsConstructor
//@Configuration
//@ConditionalOnMissingBean(value = PreConfigInterceptor.class)
//public class PostConfigInterceptor implements PostConfigProducerInterceptor {
//
//    /**
//     * Service for configuration management.
//     */
//    private final ConfigurationService configurationSvc;
//
//    /**
//     * Write config parsed by {@link de.fraunhofer.ids.messaging.core.config.ConfigProducer} to DB.
//     *
//     * @param configContainer the parsed configuration.
//     * @throws ConfigProducerInterceptorException when config cannot be written to DB.
//     */
//    @Override
//    public void perform(final ConfigContainer configContainer)
//            throws ConfigProducerInterceptorException {
//        log.info("loadconfig");
//        var config = configContainer.getConfigurationModel();
//        var configDesc = FromIdsObjectMapper.fromIdsConfig(config);
//        var configuration = configurationSvc.findActiveConfig();
//        if (configuration.isPresent()) {
//            log.info("isPresent!");
//            configurationSvc.update(configuration.get().getId(), configDesc);
//        } else {
//            log.info("else!");
//            final var dscConfig
//                    = configurationSvc.create(configDesc);
//            try {
//                log.info("swap!");
//                configurationSvc.swapActiveConfig(dscConfig.getId());
//            } catch (ConfigUpdateException e) {
//                throw new ConfigProducerInterceptorException(e.getMessage(), e.getCause());
//            }
//        }
//
//    }
//
//}
