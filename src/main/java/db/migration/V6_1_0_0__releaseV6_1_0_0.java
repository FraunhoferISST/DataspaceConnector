package db.migration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.sql.SQLException;
import java.util.UUID;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

public class V6_1_0_0__releaseV6_1_0_0 extends BaseJavaMigration {

    @Override
    public void migrate(Context ctx) throws Exception {
        migrateSubscriptions(ctx);
    }

    private void migrateSubscriptions(Context ctx) throws Exception {
        replaceByteaColumnWithStringColumn(ctx, "location");
        replaceByteaColumnWithStringColumn(ctx, "subscriber");
        replaceByteaColumnWithStringColumn(ctx, "target");
    }

    private void replaceByteaColumnWithStringColumn(Context ctx, String name) throws Exception {
        final var tmp = name + "_tmp";
        createStringColumn(ctx, tmp);
        copyByteaToString(ctx, name, tmp);
        dropColumn(ctx, name);
        renameColumn(ctx, tmp, name);
    }

    private void renameColumn(Context ctx, String name, String newName) throws SQLException {
        try(final var rename = ctx.getConnection().createStatement()) {
            rename.execute("ALTER TABLE public.subscription RENAME COLUMN " + name + " TO " + newName);
        }
    }

    private void dropColumn(Context ctx, String name) throws SQLException {
        try(final var drop = ctx.getConnection().createStatement()) {
            drop.execute("ALTER TABLE public.subscription DROP COLUMN " + name);
        }
    }

    private void copyByteaToString(Context ctx, String src, String dst) throws Exception {
        try(final var select = ctx.getConnection().createStatement()) {
            try(final var rows = select.executeQuery("SELECT id," + src + " FROM public.subscription")) {
                while(rows.next()) {
                    final var uri = toUri(rows.getBytes(2));
                    try(final var update = ctx.getConnection().createStatement()) {
                        update.execute("UPDATE public.subscription SET " + dst + "='" + uri + "' WHERE id='" + rows.getObject(1, UUID.class) + "';");
                    }
                }
            }
        }
    }

    private void createStringColumn(final Context ctx, final String name) throws SQLException {
        try(final var select = ctx.getConnection().createStatement()) {
            select.execute("ALTER TABLE public.subscription ADD COLUMN " + name + " character varying(2048);");
        }
    }

    private URI toUri(final byte[] data) throws IOException, ClassNotFoundException {
        final var stream = new ObjectInputStream(new ByteArrayInputStream(data));
        return (URI)stream.readObject();
    }
}
