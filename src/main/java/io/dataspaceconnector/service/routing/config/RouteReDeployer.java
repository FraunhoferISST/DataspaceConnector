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
package io.dataspaceconnector.service.routing.config;

import javax.annotation.PostConstruct;

import io.dataspaceconnector.common.exception.RouteCreationException;
import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.repository.RouteRepository;
import io.dataspaceconnector.service.routing.RouteHelper;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Re-deploys persisted routes in Camel on application start.
 */
@Component
@RequiredArgsConstructor
@Log4j2
@DependsOn("beanReDeployer")
public class RouteReDeployer extends TransactionCallbackWithoutResult {

    /**
     * Helper class for deploying and deleting Camel routes.
     */
    private final @NonNull RouteHelper routeHelper;

    /**
     * Transaction manager. Required for transactions in @PostConstruct methods.
     */
    private final @NonNull PlatformTransactionManager transactionManager;

    /**
     * Repository for routes.
     */
    private final @NonNull RouteRepository routeRepository;

    /**
     * Re-deploys all persisted routes in Camel.
     *
     * @param status the associated transaction status
     */
    @Override
    protected void doInTransactionWithoutResult(@NotNull final TransactionStatus status) {
        final var routes = routeRepository.findAllTopLevelRoutes();
        routes.forEach(this::redeploy);
    }

    /**
     * Redeploys a persisted route. If the corresponding Camel route cannot be created, a warning
     * is logged.
     *
     * @param route the route to redeploy.
     */
    private void redeploy(final Route route) {
        try {
            routeHelper.deploy(route);
        } catch (RouteCreationException exception) {
            log.warn("Failed to redeploy persisted route. [routeId=({}), exception=({})]",
                    route.getId(), exception.getMessage());
        }
    }

    /**
     * Loads and deploys all top-level routes from the database after application start. Thus,
     * deployed Camel routes do not get lost through a restart.
     */
    @PostConstruct
    @Transactional(readOnly = true)
    public void redeploySavedRoutes() {
        final var template = new TransactionTemplate(transactionManager);
        template.execute(this);
    }
}
