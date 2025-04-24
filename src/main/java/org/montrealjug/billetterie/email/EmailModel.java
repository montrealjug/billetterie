// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;

public class EmailModel {

    // to use in email templates via Email implementation utility methods
    private static final DateTimeFormatter FR_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter EN_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy").withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("H:mm a").withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter ISO_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter ISO_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withLocale(Locale.CANADA_FRENCH);

    private EmailModel() {
        // model class
    }

    public enum EmailType {
        AFTER_BOOKING;

        public String subjectKey() {
            return this.name().toLowerCase();
        }

        public String plainTextTemplate() {
            return "email/%s-plain.jte".formatted(this.name().toLowerCase());
        }

        public String htmlTextTemplate() {
            return "email/%s-html.jte".formatted(this.name().toLowerCase());
        }
    }

    public interface Email {

        EmailType type();

        InternetAddress to();

        default String formatDateFr(Temporal temporal) {
            return FR_DATE_FORMAT.format(temporal);
        }

        default String formatDateEn(Temporal temporal) {
            return EN_DATE_FORMAT.format(temporal);
        }

        default String formatTime(Temporal temporal) {
            return TIME_FORMAT.format(temporal);
        }

        default String formatForDateTimeAttribute(Temporal temporal) {
            if (temporal.isSupported(ChronoField.HOUR_OF_DAY)) {
                return ISO_DATE_TIME_FORMAT.format(temporal);
            } else if (temporal.isSupported(ChronoField.DAY_OF_MONTH)) {
                return ISO_DATE_FORMAT.format(temporal);
            } else {
                throw new UnsupportedOperationException(
                        "Unsupported temporal type: " + temporal.getClass());
            }
        }

        static Email afterBooking(
                Booker booker, Event event, Set<ActivityParticipant> participants) {
            return new AfterBookingEmail(booker, event, participants);
        }

        private static InternetAddress fromBooker(Booker booker) {
            try {
                return new InternetAddress(
                        booker.getEmail(),
                        "%s %s".formatted(booker.getFirstName(), booker.getLastName()),
                        StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    // must be public to be used in jte template
    public record AfterBookingEmail(
            Booker booker, Event event, Set<ActivityParticipant> participants) implements Email {

        public AfterBookingEmail(
                Booker booker, Event event, Set<ActivityParticipant> participants) {
            this.booker = booker;
            this.event = event;
            this.participants =
                    participants instanceof SortedSet ? participants : new TreeSet<>(participants);
        }

        @Override
        public EmailType type() {
            return EmailType.AFTER_BOOKING;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return "https://placeholder_for_registration_link.test";
        }
    }

    record EmailToSend(InternetAddress to, String subject, String plainText, String html) {}
}
