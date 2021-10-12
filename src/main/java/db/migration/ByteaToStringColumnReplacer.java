package db.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.api.migration.Context;

public class ByteaToStringColumnReplacer {

    private final String table;

    public ByteaToStringColumnReplacer(final String table) { this.table = table; }

    public void replace(final Context ctx, final String name, int length) throws Exception {
        final var tmp = name + "_tmp";
        createStringColumn(ctx, tmp, length);
        copyByteaToString(ctx, name, tmp);
        dropColumn(ctx, name);
        renameColumn(ctx, tmp, name);
    }

    private void renameColumn(Context ctx, String name, String newName) throws SQLException {
        try(final var rename = ctx.getConnection().createStatement()) {
            rename.execute("ALTER TABLE " + table + " RENAME COLUMN " + name + " TO " + newName);
        }
    }

    private void dropColumn(Context ctx, String name) throws SQLException {
        try(final var drop = ctx.getConnection().createStatement()) {
            drop.execute("ALTER TABLE " + table + " DROP COLUMN " + name);
        }
    }

    private void copyByteaToString(Context ctx, String src, String dst) throws Exception {
        try(final var select = ctx.getConnection().createStatement()) {
            try(final var rows = select.executeQuery("SELECT id," + src + " FROM " + table)) {
                while(rows.next()) {
                    final var uri = toUri(rows.getBytes(2));
                    try(final var update = ctx.getConnection().createStatement()) {
                        update.execute("UPDATE " + table + " SET " + dst + "='" + uri + "' WHERE id='" + rows.getObject(1, UUID.class) + "';");
                    }
                }
            }
        }
    }

    private void createStringColumn(final Context ctx, final String name, final int length) throws SQLException {
        try(final var select = ctx.getConnection().createStatement()) {
            select.execute("ALTER TABLE " + table + " ADD COLUMN " + name + " character varying("+ length +");");
        }
    }

    private URI toUri(final byte[] data) throws IOException, ClassNotFoundException {
        final var stream = new ObjectInputStream(new ByteArrayInputStream(data));
        return (URI)stream.readObject();
    }
}
