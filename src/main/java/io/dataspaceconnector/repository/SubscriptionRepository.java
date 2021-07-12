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
package io.dataspaceconnector.repository;

import io.dataspaceconnector.model.Subscription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;

/**
 * The repository containing all objects of type {@link Subscription}.
 */
@Repository
public interface SubscriptionRepository extends BaseEntityRepository<Subscription> {

    /**
     * Finds all subscriptions with the current connector as subscriber.
     *
     * @param subscriber URI of the subscriber.
     * @return List of all subscription with the subscriber.
     */
    @Query("SELECT r "
            + "FROM Subscription r "
            + "WHERE r.subscriber = :subscriber "
            + "AND r.deleted = false")
    List<Subscription> findAllBySubscriber(URI subscriber);
}
