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

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.net.URI;
import java.util.List;

import io.dataspaceconnector.model.RegistrationStatus;
import io.dataspaceconnector.model.apps.App;
import io.dataspaceconnector.model.base.AbstractEntity;
import io.dataspaceconnector.model.utils.UriConverter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * Apps can be downloaded from an app store to perform data operations on the data.
 */
@Entity
@Table(name = "appstore")
@SQLDelete(sql = "UPDATE appstore SET deleted=true WHERE id=?")
@Where(clause = "deleted = false")
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class AppStore extends AbstractEntity {

    /**
     * Serial version uid.
     **/
    private static final long serialVersionUID = 1L;

    /**
     * The access url of the app store.
     */
    @Convert(converter = UriConverter.class)
    private URI name;

    /**
     * The title of the app store.
     */
    private String title;

    /**
     * The registration status.
     */
    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    /**
     * The list of apps.
     */
    @OneToMany
    private List<App> appList;
}
