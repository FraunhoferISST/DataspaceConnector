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
package io.dataspaceconnector.services.configuration;

import io.dataspaceconnector.model.route.Route;
import io.dataspaceconnector.model.route.RouteDesc;
import io.dataspaceconnector.repositories.EndpointRepository;
import io.dataspaceconnector.services.resources.BaseEntityService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for routes.
 */
@Service
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.NONE)
public class RouteService extends BaseEntityService<Route, RouteDesc> {

    /**
     * Repository for storing data.
     **/
    private final @NonNull EndpointRepository endpointRepo;

    @Autowired
    public RouteService(final @NonNull EndpointRepository endpointRepository){
        this.endpointRepo = endpointRepository;
    }

    @Override
    protected Route persist(final Route route){
        if(route.getStart()!=null){
            endpointRepo.save(route.getStart());
        }
        if(route.getEnd()!=null){
            endpointRepo.save(route.getEnd());
        }
        return super.persist(route);
    }
}
