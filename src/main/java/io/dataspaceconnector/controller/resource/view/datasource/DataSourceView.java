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
package io.dataspaceconnector.controller.resource.view.datasource;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.dataspaceconnector.config.BaseType;
import io.dataspaceconnector.controller.resource.view.util.ViewConstants;
import io.dataspaceconnector.model.datasource.DataSourceType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * A DTO for controlled exposing of data source information in API responses.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Relation(collectionRelation = BaseType.DATA_SOURCES, itemRelation = "datasource")
public class DataSourceView extends RepresentationModel<DataSourceView> {

    /**
     * The creation date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime creationDate;

    /**
     * The last modification date.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ViewConstants.DATE_TIME_FORMAT)
    private ZonedDateTime modificationDate;

    /**
     * The id of the data source.
     */
    private UUID id;

    /**
     * The type of the data source.
     */
    private DataSourceType type;
}
