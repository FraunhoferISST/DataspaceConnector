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

import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Performs migration operations for v6.5.0.
 */
public class V6_5_0__releaseV6_5_0 extends BaseJavaMigration {

    /**
     * Serializer for Infomodel types.
     */
    private final Serializer serializer = new Serializer();

    /**
     * Select query for getting ID and value of agreements.
     */
    private static final String SELECT_AGREEMENTS = "SELECT id, lo_get(value::oid) FROM public.agreement";

    /**
     * Prepared update query for setting the value field of agreements.
     */
    private static final String SET_VALUE = "UPDATE public.agreement"
            + " SET value=lo_from_bytea(0, ?) WHERE id=?";

    /**
     * Performs the migration.
     *
     * @param ctx The Flyway context.
     * @throws Exception if any error occurs.
     */
    @Override
    public void migrate(final Context ctx) throws Exception {
        migrateAgreements(ctx);
    }

    /**
     * Migrates agreements. Sets new date with {@link LocalDateTime#MAX} with {@link ZoneId}
     * UTC as the agreements endDate, if the endDate was previously null.
     *
     * @param ctx The Flyway context.
     * @throws SQLException if any error occurs when performing database operations.
     * @throws IOException if an error occurs when (de)serializing agreements.
     * @throws DatatypeConfigurationException if an error occurs when creating the date.
     */
    @SuppressFBWarnings(value={"RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE",
            "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE"})
    private void migrateAgreements(final Context ctx) throws SQLException, IOException,
            DatatypeConfigurationException {
        final var endDate = getEndDate();

        try (var select = ctx.getConnection().createStatement()) {
            try (var rows = select.executeQuery(SELECT_AGREEMENTS)) {
                while (rows.next()) {
                    final var id = rows.getObject(1, UUID.class);
                    final var agreementValue = rows.getBytes(2);
                    final var agreementString = new String(agreementValue, StandardCharsets.UTF_8);
                    final var agreement = serializer
                            .deserialize(agreementString, ContractAgreement.class);
                    if (agreement.getContractEnd() == null) {
                        agreement.setContractEnd(endDate);
                        final var updatedValue = serializer.serialize(agreement);
                        try (var update = ctx.getConnection().prepareStatement(SET_VALUE)) {
                            update.setBytes(1, updatedValue.getBytes(StandardCharsets.UTF_8));
                            update.setObject(2, id);
                            update.execute();
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates the end date for the agreements.
     *
     * @return the end date.
     * @throws DatatypeConfigurationException if an error occurs when creating the date.
     */
    private XMLGregorianCalendar getEndDate() throws DatatypeConfigurationException {
        final var calendar = new GregorianCalendar();
        calendar.set(2100, Calendar.DECEMBER, 31, 23, 59, 59);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

}
