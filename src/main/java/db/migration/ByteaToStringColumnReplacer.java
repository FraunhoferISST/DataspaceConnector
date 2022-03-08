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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.api.migration.Context;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Helper class that changes the type of a column from Bytea (URI) to string and migrated the data
 * accordingly.
 */
@SuppressFBWarnings(value={"SQL_INJECTION_JDBC", "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE",
        "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
        "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
public class ByteaToStringColumnReplacer {

    /**
     * Prepared query for adding a column to a table.
     */
    private static final String ADD_COLUMN = "ALTER TABLE %s ADD COLUMN %s character varying(%d);";

    /**
     * Prepared query for getting all non-empty entries in a column.
     */
    private static final String SELECT_FROM_COLUMN = "SELECT id,%s FROM %s WHERE %s IS NOT NULL;";

    /**
     * Prepared query for updating the value in a column.
     */
    private static final String UPDATE_COLUMN = "UPDATE %s SET %s='%s' WHERE id='%s';";

    /**
     * Prepared query for dropping a column.
     */
    private static final String DROP_COLUMN = "ALTER TABLE %s DROP COLUMN %s";

    /**
     * Prepared query for renaming a column.
     */
    private static final String RENAME_COLUMN = "ALTER TABLE %s RENAME COLUMN %s TO %s";

    /**
     * Name of the table on which to change the column type.
     */
    private final String table;

    /**
     * Constructs a new ByteaToStringColumnReplacer with the table name.
     *
     * @param tableName Name of the table on which to change the column type.
     */
    public ByteaToStringColumnReplacer(final String tableName) {
        this.table = tableName;
    }

    /**
     * Changes the type of a column from Bytea to String with the given length.
     *
     * @param ctx The Flyway context.
     * @param name Name of the column.
     * @param length New length of the column.
     * @throws Exception if any error occurs.
     */
    public void replace(final Context ctx, final String name, final int length) throws Exception {
        final var tmp = name + "_tmp";
        createStringColumn(ctx, tmp, length);
        copyByteaToString(ctx, name, tmp);
        dropColumn(ctx, name);
        renameColumn(ctx, tmp, name);
    }

    /**
     * Renames a column.
     *
     * @param ctx The Flyway context.
     * @param name Name of the column.
     * @param newName New name of the column.
     * @throws SQLException if any error occurs.
     */
    private void renameColumn(final Context ctx, final String name, final String newName)
            throws SQLException {
        try (var rename = ctx.getConnection().createStatement()) {
            rename.execute(String.format(RENAME_COLUMN, table, name, newName));
        }
    }

    /**
     * Drops a column.
     *
     * @param ctx The Flyway context.
     * @param name Name of the column.
     * @throws SQLException if any error occurs when performing database operations.
     */
    private void dropColumn(final Context ctx, final String name) throws SQLException {
        try (var drop = ctx.getConnection().createStatement()) {
            drop.execute(String.format(DROP_COLUMN, table, name));
        }
    }

    /**
     * Copies all URIs from a Bytea type column to a string type column.
     *
     * @param ctx The Flyway context.
     * @param src Name of the source column.
     * @param dst Name of the destination column.
     * @throws Exception if any error occurs.
     */
    private void copyByteaToString(final Context ctx, final String src, final String dst)
            throws Exception {
        try (var select = ctx.getConnection().createStatement()) {
            try (var rows = select.executeQuery(String
                    .format(SELECT_FROM_COLUMN, src, table, src))) {
                while (rows.next()) {
                    final var uri = toUri(rows.getBytes(2));
                    try (var update = ctx.getConnection().createStatement()) {
                        final var id = rows.getObject(1, UUID.class);
                        update.execute(String.format(UPDATE_COLUMN, table, dst, uri, id));
                    }
                }
            }
        }
    }

    /**
     * Creates a new column of type string.
     *
     * @param ctx The Flyway context.
     * @param name Name of the column.
     * @param length Length of the column.
     * @throws SQLException if any error occurs when performing database operations.
     */
    private void createStringColumn(final Context ctx, final String name, final int length)
            throws SQLException {
        try (var select = ctx.getConnection().createStatement()) {
            select.execute(String.format(ADD_COLUMN, table, name, length));
        }
    }

    /**
     * Converts a byte array to a URI.
     *
     * @param data The byte array.
     * @return The URI.
     * @throws IOException if reading the byte array fails.
     * @throws ClassNotFoundException if the byte array content cannot be casted to the desired
     *                                class.
     */
    @SuppressFBWarnings("OBJECT_DESERIALIZATION")
    private URI toUri(final byte[] data) throws IOException, ClassNotFoundException {
        final var stream = new ObjectInputStream(new ByteArrayInputStream(data));
        return (URI) stream.readObject();
    }
}
