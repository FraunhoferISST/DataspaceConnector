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

import io.dataspaceconnector.model.datasource.DataSource;
import io.dataspaceconnector.model.datasource.DataSourceDesc;
import io.dataspaceconnector.model.datasource.DataSourceFactory;
import io.dataspaceconnector.model.datasource.RestDataSourceDesc;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataSourceViewAssemblerTest {

    @Test
    public void create_ValidDataSource_returnDataSourceView() {
        /* ARRANGE */
        final var shouldLookLike = getDataSource();

        /* ACT */
        final var after = getDataSourceView();

        /* ASSERT */
        assertEquals(after.getType(), shouldLookLike.getType());
    }

    private DataSource getDataSource() {
        final var factory = new DataSourceFactory();
        return factory.create(getDataSourceDesc());
    }

    private DataSourceDesc getDataSourceDesc() {
        return new RestDataSourceDesc();
    }

    private DataSourceView getDataSourceView(){
        final var assembler = new DataSourceViewAssembler();
        return assembler.toModel(getDataSource());
    }
}
