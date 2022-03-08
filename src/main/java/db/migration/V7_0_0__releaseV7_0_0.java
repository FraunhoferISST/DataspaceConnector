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
package db.migration;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

/**
 * Performs migration operations for v7.0.0 in addition to the migration script.
 */
@SuppressWarnings("typename")
public class V7_0_0__releaseV7_0_0 extends BaseJavaMigration {

    /**
     * Select query for getting ID and value of local data instances.
     */
    private static final String SELECT_LOCAL_DATA = "SELECT id,lo_get(value::oid) FROM public.data"
            + " WHERE value IS NOT NULL;";

    /**
     * Prepared update query for setting the value field of local data.
     */
    private static final String SET_VALUE = "UPDATE public.data SET value=lo_from_bytea(0, ?) WHERE id=?;";

    /**
     * Performs the migration.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    @Override
    public void migrate(final Context ctx) throws Exception {
        migrateLocalData(ctx);
    }

    /**
     * Migrates local data. Changes the encoding of value from UTF-16 to UTF-8.
     *
     * @param ctx The Flyway context.
     * @throws SQLException if any error occurs when performing database operations.
     */
    @SuppressFBWarnings(value={"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
    private void migrateLocalData(final Context ctx) throws SQLException {
        try (var select = ctx.getConnection().createStatement()) {
            try (var rows = select.executeQuery(SELECT_LOCAL_DATA)) {
                while (rows.next()) {
                    final var id = rows.getObject(1, Long.class);
                    final var utf16Encoded = rows.getBytes(2);
                    final var data = new String(utf16Encoded, StandardCharsets.UTF_16);
                    final var utf8Encoded = data.getBytes(StandardCharsets.UTF_8);
                    try (var update = ctx.getConnection().prepareStatement(SET_VALUE)) {
                        update.setBytes(1, utf8Encoded);
                        update.setObject(2, id);
                        update.execute();
                    }
                }
            }
        }
    }
}
