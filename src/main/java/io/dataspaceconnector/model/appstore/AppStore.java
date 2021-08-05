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
package io.dataspaceconnector.model.appstore;


import io.dataspaceconnector.model.app.App;
import io.dataspaceconnector.model.named.NamedEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

/**
 * App store to distribute apps.
 */
@javax.persistence.Entity
@Table(name = "appstore")
@SQLDelete(sql = "UPDATE appstore SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AppStore extends NamedEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * App store location.
     */
    private URI location;

    /**
     * List of apps in app store.
     */
    @OneToMany(fetch = FetchType.EAGER)
    private List<App> apps;

}
