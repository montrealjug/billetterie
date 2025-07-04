// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.*;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.ui.PresentationActivityParticipant;

public class EmailModel {

    // to use in email templates via Email implementation utility methods
    private static final DateTimeFormatter FR_DATE_FORMAT = DateTimeFormatter
        .ofPattern("dd/MM/yyyy")
        .withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter EN_DATE_FORMAT = DateTimeFormatter
        .ofPattern("MM/dd/yyyy")
        .withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter
        .ofPattern("H:mm a")
        .withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter ISO_DATE_TIME_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        .withLocale(Locale.CANADA_FRENCH);
    private static final DateTimeFormatter ISO_DATE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd")
        .withLocale(Locale.CANADA_FRENCH);

    private EmailModel() {
        // model class
    }

    public enum EmailType {
        AFTER_BOOKING,
        AFTER_REGISTRATION,
        AFTER_PARTICIPANTS_CHANGES,
        PARTICIPANT_UPGRADED,
        RETURNING_BOOKER,
        LAST_REMINDER;

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

        default Optional<byte[]> qrCode() {
            return Optional.empty();
        }

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
                throw new UnsupportedOperationException("Unsupported temporal type: " + temporal.getClass());
            }
        }

        static Email afterBooking(Booker booker, Event event, Set<ActivityParticipant> participants) {
            return new AfterBookingEmail(booker, event, participants);
        }

        static Email afterRegistration(Booker booker, String baseUrl) {
            return new AfterRegistrationEmail(booker, baseUrl);
        }

        static Email afterParticipantsChanges(
            Booker booker,
            List<PresentationActivityParticipant> participants,
            Event event,
            String baseUrl,
            byte[] qrCode
        ) {
            return new AfterParticipantsChangesEmail(booker, participants, event, baseUrl, Optional.of(qrCode));
        }

        static Email participantUpgraded(
            Booker booker,
            PresentationActivityParticipant participantActivity,
            Event event,
            String baseUrl,
            byte[] qrCode
        ) {
            return new ParticipantUpgraded(booker, participantActivity, event, baseUrl, Optional.of(qrCode));
        }

        static Email returningBooker(Booker booker, String baseUrl) {
            return new ReturningBookingEmail(booker, baseUrl);
        }

        static Email lastReminder(
            Booker booker,
            List<PresentationActivityParticipant> participants,
            Event event,
            String baseUrl,
            byte[] qrCode
        ) {
            return new LastReminderEmail(booker, participants, event, baseUrl, Optional.of(qrCode));
        }

        private static InternetAddress fromBooker(Booker booker) {
            try {
                return new InternetAddress(
                    booker.getEmail(),
                    "%s %s".formatted(booker.getFirstName(), booker.getLastName()),
                    StandardCharsets.UTF_8.name()
                );
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    // must be public to be used in jte template
    public record AfterBookingEmail(Booker booker, Event event, Set<ActivityParticipant> participants)
        implements Email {
        public AfterBookingEmail(Booker booker, Event event, Set<ActivityParticipant> participants) {
            this.booker = booker;
            this.event = event;
            this.participants = participants instanceof SortedSet ? participants : new TreeSet<>(participants);
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

    public record AfterRegistrationEmail(Booker booker, String baseUrl) implements Email {
        @Override
        public EmailType type() {
            return EmailType.AFTER_REGISTRATION;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return baseUrl + "/bookings/" + booker.getEmailSignature();
        }
    }

    public record AfterParticipantsChangesEmail(
        Booker booker,
        List<PresentationActivityParticipant> participants,
        Event event,
        String baseUrl,
        Optional<byte[]> qrCode
    )
        implements Email {
        @Override
        public EmailType type() {
            return EmailType.AFTER_PARTICIPANTS_CHANGES;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return baseUrl + "/bookings/" + booker.getEmailSignature();
        }
    }

    public record ParticipantUpgraded(
        Booker booker,
        PresentationActivityParticipant participant,
        Event event,
        String baseUrl,
        Optional<byte[]> qrCode
    )
        implements Email {
        @Override
        public EmailType type() {
            return EmailType.PARTICIPANT_UPGRADED;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return baseUrl + "/bookings/" + booker.getEmailSignature();
        }
    }

    public record ReturningBookingEmail(Booker booker, String baseUrl) implements Email {
        @Override
        public EmailType type() {
            return EmailType.RETURNING_BOOKER;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return baseUrl + "/bookings/" + booker.getEmailSignature();
        }
    }

    public record LastReminderEmail(
        Booker booker,
        List<PresentationActivityParticipant> participants,
        Event event,
        String baseUrl,
        Optional<byte[]> qrCode
    )
        implements Email {
        @Override
        public EmailType type() {
            return EmailType.LAST_REMINDER;
        }

        @Override
        public InternetAddress to() {
            return Email.fromBooker(booker);
        }

        public String registrationLink() {
            return baseUrl + "/bookings/" + booker.getEmailSignature();
        }
    }

    record EmailToSend(
        InternetAddress to,
        String subject,
        String plainText,
        String html,
        Optional<byte[]> attachment
    ) {}
}
