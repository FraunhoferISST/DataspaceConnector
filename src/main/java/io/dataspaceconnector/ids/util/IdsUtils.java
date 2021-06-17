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
package io.dataspaceconnector.ids.util;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.fraunhofer.iais.eis.Language;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import lombok.SneakyThrows;

public final class IdsUtils {

    /**
     * Default constructor.
     */
    private IdsUtils() {
        // not used
    }

    /**
     * Get list of keywords as ids list of typed literals.
     *
     * @param keywords List of keywords.
     * @param language The language.
     * @return List of typed literal.
     */
    public static List<TypedLiteral> getKeywordsAsTypedLiteral(final List<String> keywords,
                                                 final String language) {
        final var idsKeywords = new ArrayList<TypedLiteral>();
        for (final var keyword : keywords) {
            idsKeywords.add(new TypedLiteral(keyword, language));
        }

        return idsKeywords;
    }

    /**
     * Convert string to ids language.
     *
     * @param language The language as string.
     * @return The ids language object.
     */
    public static Language getLanguage(final String language) {
        switch (language.toLowerCase(Locale.ENGLISH)) {
            case "de":
                return Language.DE;
            case "en":
            default:
                return Language.EN;
        }
    }

    /**
     * Get list of ids keywords as list of strings.
     * If the passed list is null, an empty list is returned.
     * @param keywords List of typed literals.
     * @return List of strings.
     */
    public static List<String> getKeywordsAsString(final List<? extends TypedLiteral> keywords) {

        final var list = new ArrayList<String>();
        if (keywords != null) {
            for (final var keyword : keywords) {
                list.add(keyword.getValue());
            }
        }

        return list;
    }

    /**
     * Converts a date to XMLGregorianCalendar format.
     *
     * @param date the date object.
     * @return the XMLGregorianCalendar object or null.
     */
    @SneakyThrows
    public static XMLGregorianCalendar getGregorianOf(final ZonedDateTime date) {
        final var calendar = GregorianCalendar.from(date);
        calendar.setTimeZone(TimeZone.getTimeZone(ZoneId.ofOffset("", ZoneOffset.UTC)));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
