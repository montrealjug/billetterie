// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import static org.assertj.core.api.Assertions.assertThat;

import gg.jte.TemplateEngine;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.support.ResourceBundleMessageSource;

@SpringBootTest
class EmailWriterTest {

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    ResourceBundleMessageSource messageSource;

    EmailWriter emailWriter;

    @BeforeEach
    void setUp() {
        emailWriter = new EmailWriter(templateEngine, messageSource);
    }

    @ParameterizedTest
    @MethodSource("emails")
    void write_should_write_expected_emails(Email email) {
        var emailToSend = emailWriter.write(email);

        assertThat(emailToSend.to()).isEqualTo(email.to());
        var expectedSubject = messageSource.getMessage(email.type().name().toLowerCase(), null, Locale.CANADA_FRENCH);
        assertThat(emailToSend.subject()).isEqualTo(expectedSubject);
        var expectedPlainTextFile = "email/%s.txt".formatted(email.type().name().toLowerCase());
        var plainTextContent = EmailTestHelper.loadResourceContent(expectedPlainTextFile);
        assertThat(emailToSend.plainText().trim()).isEqualTo(plainTextContent.trim());
        var expectedHtmlFile = "email/%s.html".formatted(email.type().name().toLowerCase());
        var expectedHtmlContent = EmailTestHelper.loadResourceContent(expectedHtmlFile);
        assertThat(Jsoup.parse(emailToSend.html()).html()).isEqualTo(Jsoup.parse(expectedHtmlContent).html());
    }

    static Stream<Arguments> emails() {
        return Stream.of(Arguments.of(forAfterBooking()), Arguments.of(forReturningBooker()));
    }

    static Email forAfterBooking() {
        var event = new Event();
        event.setDate(LocalDate.of(2025, 4, 15));
        event.setTitle("Test Event");
        var activity = new Activity();
        activity.setId(1L);
        activity.setTitle("Test Activity");
        activity.setStartTime(LocalDateTime.of(2025, 4, 15, 13, 25, 0));
        var booker = new Booker();
        booker.setEmail("booker@test.org");
        booker.setFirstName("booker-firstName-é");
        booker.setLastName("booker-lastName-ü");
        var firstParticipant = new Participant();
        firstParticipant.setBooker(booker);
        firstParticipant.setFirstName("part-firstName-é");
        firstParticipant.setLastName("part-lastName-ü");
        firstParticipant.setId(1L);
        var firstActivityParticipant = new ActivityParticipant();
        firstActivityParticipant.setActivity(activity);
        firstActivityParticipant.setParticipant(firstParticipant);
        firstActivityParticipant.getActivityParticipantKey().setActivityId(activity.getId());
        firstActivityParticipant.getActivityParticipantKey().setParticipantId(firstParticipant.getId());
        var secondParticipant = new Participant();
        secondParticipant.setBooker(booker);
        secondParticipant.setFirstName("second-firstName-é");
        secondParticipant.setLastName("second-lastName-ü");
        secondParticipant.setId(2L);
        var secondActivityParticipant = new ActivityParticipant();
        secondActivityParticipant.setActivity(activity);
        secondActivityParticipant.setParticipant(secondParticipant);
        secondActivityParticipant.getActivityParticipantKey().setActivityId(activity.getId());
        secondActivityParticipant.getActivityParticipantKey().setParticipantId(secondParticipant.getId());
        var participants = Set.of(firstActivityParticipant, secondActivityParticipant);
        return Email.afterBooking(booker, event, participants);
    }

    static Email forReturningBooker() {
        var booker = new Booker();
        booker.setEmail("email@test.org");
        booker.setFirstName("booker-firstName");
        booker.setLastName("booker-lastName");
        booker.setEmailSignature("signature");
        var baseUrl = "base_url";
        return Email.returningBooker(booker, baseUrl);
    }
}
