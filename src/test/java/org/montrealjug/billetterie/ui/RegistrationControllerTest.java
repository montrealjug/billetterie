// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.xml.element.NodeChildren;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailModel.EmailType;
import org.montrealjug.billetterie.email.EmailService;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.montrealjug.billetterie.repository.EventRepository;
import org.montrealjug.billetterie.service.SignatureService;
import org.montrealjug.billetterie.ui.RegistrationController.BookerCheck;
import org.montrealjug.billetterie.ui.RegistrationController.ParticipantSubmission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class RegistrationControllerTest {

    @LocalServerPort private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        // dirty trick to make the test idempotent without
        // dropping all data from the container to not impact local dev.
        // we can't use spring's @Transactional because the transaction
        // won't be committed and the data won't be available in the web layer
        // that shouldn't be done, but it will ease dev life
        bookerRepository.deleteAllById(CREATED_BOOKER_IDS);
        eventRepository.deleteAllById(CREATED_EVENT_IDS);
        CREATED_BOOKER_IDS.clear();
        CREATED_EVENT_IDS.clear();
    }

    static final Set<String> CREATED_BOOKER_IDS = new HashSet<>();
    static final Set<Long> CREATED_EVENT_IDS = new HashSet<>();

    @Autowired BookerRepository bookerRepository;

    @Autowired EventRepository eventRepository;

    @MockitoBean SignatureService signatureService;

    @MockitoSpyBean EmailService emailService;

    @Captor ArgumentCaptor<Email> emailCaptor;

    @Test
    void retrieveBaseUrlTest() {
        // Create a mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        // Set up the mock to return specific values
        var requestURL = new StringBuffer("http://localhost:8080/some/path");
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getRequestURI()).thenReturn("/some/path");
        when(request.getContextPath()).thenReturn("");

        // Call the method and verify the result
        String baseUrl = RegistrationController.retrieveBaseUrl(request);
        assertEquals("http://localhost:8080", baseUrl);

        // Test with a different URL and context path
        requestURL = new StringBuffer("https://example.com/some/path");
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getRequestURI()).thenReturn("/some/path");
        when(request.getContextPath()).thenReturn("/app");

        baseUrl = RegistrationController.retrieveBaseUrl(request);
        assertEquals("https://example.com/app", baseUrl);
    }

    @Test
    public void isSameParticipantTest() {
        // Create a Participant
        Participant participant = new org.montrealjug.billetterie.entity.Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        participant.setYearOfBirth(1990);

        // Test with matching submission
        ParticipantSubmission matchingSubmission =
                new ParticipantSubmission("John", "Doe", 1990, 1L, "signature");
        boolean result = RegistrationController.isSameParticipant(participant, matchingSubmission);
        assertTrue(result, "Should return true for matching participant");

        // Test with different first name
        ParticipantSubmission differentFirstNameSubmission =
                new ParticipantSubmission("Jane", "Doe", 1990, 1L, "signature");
        result =
                RegistrationController.isSameParticipant(participant, differentFirstNameSubmission);
        assertFalse(result, "Should return false for different first name");

        // Test with different last name
        ParticipantSubmission differentLastNameSubmission =
                new ParticipantSubmission("John", "Smith", 1990, 1L, "signature");
        result = RegistrationController.isSameParticipant(participant, differentLastNameSubmission);
        assertFalse(result, "Should return false for different last name");

        // Test with different year of birth
        ParticipantSubmission differentYearSubmission =
                new ParticipantSubmission("John", "Doe", 1991, 1L, "signature");
        result = RegistrationController.isSameParticipant(participant, differentYearSubmission);
        assertFalse(result, "Should return false for different year of birth");

        // Test case insensitivity
        ParticipantSubmission caseInsensitiveSubmission =
                new ParticipantSubmission("JOHN", "DOE", 1990, 1L, "signature");
        result = RegistrationController.isSameParticipant(participant, caseInsensitiveSubmission);
        assertTrue(result, "Should return true for case-insensitive match");
    }

    @Test
    void checkReturningBooker_should_return_404_if_no_booker_found_without_sending_any_email() {
        var email = "not-known-email@test.org";
        var bookerCheck = new BookerCheck(email);

        given().contentType(ContentType.JSON)
                .body(bookerCheck)
                .when()
                .post("/check-returning-booker")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

        verifyNoInteractions(emailService);
    }

    @Test
    void
            checkReturningBooker_should_return_204_sending_RETURNING_BOOKER_email_if_booker_known_and_confirmed() {
        var email = "returning-booker@test.org";
        createBooker(email, true);
        var bookerCheck = new BookerCheck(email);

        given().contentType(ContentType.JSON)
                .body(bookerCheck)
                .when()
                .post("/check-returning-booker")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(emailService).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().type()).isSameAs(EmailType.RETURNING_BOOKER);
    }

    @Test
    void
            checkReturningBooker_should_return_204_sending_AFTER_REGISTRATION_email_if_booker_known_and_not_confirmed() {
        var email = "not-verified-booker@test.org";
        createBooker(email, false);
        var bookerCheck = new BookerCheck(email);

        given().contentType(ContentType.JSON)
                .body(bookerCheck)
                .when()
                .post("/check-returning-booker")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        verify(emailService).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().type()).isSameAs(EmailType.AFTER_REGISTRATION);
    }

    @Test
    void
            registerBooker_should_return_a_201_after_saving_booker_in_db_and_sending_AFTER_REGISTRATION_email()
                    throws Exception {
        var email = "registration-booker@test.org";
        when(signatureService.signAndTrim(email)).thenReturn(SIGNATURE);
        var booker = new PresentationBooker("New", "Booker", email);
        CREATED_BOOKER_IDS.add(email);

        given().contentType(ContentType.JSON)
                .body(booker)
                .when()
                .post("/register-booker")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        var savedBooker = bookerRepository.findById(email);
        assertThat(savedBooker)
                .hasValueSatisfying(
                        b -> {
                            assertThat(b.getFirstName()).isEqualTo(booker.firstName());
                            assertThat(b.getLastName()).isEqualTo(booker.lastName());
                            assertThat(b.getEmail()).isEqualTo(booker.email());
                            assertThat(b.getEmailSignature()).isEqualTo(SIGNATURE);
                            assertThat(b.getValidationTime()).isNull();
                        });
        verify(emailService).sendEmail(emailCaptor.capture());
        assertThat(emailCaptor.getValue().type()).isSameAs(EmailType.AFTER_REGISTRATION);
    }

    @Test
    void registerBooker_should_return_a_409_without_sending_email_if_email_is_known() {
        var email = "already-registered@test.org";
        createBooker(email, false);
        var booker = new PresentationBooker("Other First Name", "Other Last Name", email);

        given().contentType(ContentType.JSON)
                .body(booker)
                .when()
                .post("/register-booker")
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
        verifyNoInteractions(emailService, signatureService);
    }

    @Test
    void startBooking_should_display_bookers_activity_if_Booker_exists_and_an_Event_is_active() {
        // create a Booker
        var email = "start-booking@test.org";
        createBooker(email, false);
        // get the active Event (creates it if needed)
        var event = getOrCreateActiveEvent();
        // keep track of time
        var beforeCall = Instant.now();

        var htmlPath =
                given().pathParam("signature", SIGNATURE)
                        .when()
                        .get("/bookings/{signature}")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .contentType(ContentType.HTML)
                        .extract()
                        .htmlPath();

        // check the title to ensure we are in the html generated by the `booker-activities.jte`
        // template
        assertThat((String) htmlPath.get("html.head.title")).isEqualTo("Event Activities");
        // check that booker validationTime has been updated
        var validatedBooker =
                bookerRepository
                        .findById(email)
                        .orElseThrow(() -> new IllegalStateException("Booker not found"));
        assertThat(validatedBooker.getValidationTime()).isBetween(beforeCall, Instant.now());
    }

    @Test
    void startBooking_should_not_update_validationTime_if_already_set() {
        // create an already validated Booker
        var email = "start-bookingr@test.org";
        var booker = createBooker(email, true);
        // get the active Event (creates it if needed)
        getOrCreateActiveEvent();

        var htmlPath =
                given().pathParam("signature", SIGNATURE)
                        .when()
                        .get("/bookings/{signature}")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .contentType(ContentType.HTML)
                        .extract()
                        .htmlPath();

        // check the title to ensure we are in the html generated by the `booker-activities.jte`
        // template
        assertThat((String) htmlPath.get("html.head.title")).isEqualTo("Event Activities");
        // check that booker validationTime has been updated
        var validatedBooker =
                bookerRepository
                        .findById(email)
                        .orElseThrow(() -> new IllegalStateException("Booker not found"));
        // compare with millis, because we lose precision in the DB
        assertThat(validatedBooker.getValidationTime().truncatedTo(ChronoUnit.MILLIS))
                .isEqualTo(booker.getValidationTime().truncatedTo(ChronoUnit.MILLIS));
    }

    @Test
    void startBooking_should_display_index_if_no_Booker_found_while_displaying_an_error_message() {
        // create an already validated Booker
        var email = "start-booking@test.org";
        createBooker(email, true);
        // get the active Event (creates it if needed)
        getOrCreateActiveEvent();

        var htmlPath =
                given().pathParam("signature", "not-known-signature")
                        .when()
                        .get("/bookings/{signature}")
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .contentType(ContentType.HTML)
                        .extract()
                        .htmlPath();

        // check the title to ensure we are in the html generated by the `index.jte`
        // template
        assertThat((String) htmlPath.get("html.head.title")).isEqualTo("Event List");
        // check that the error msg is present
        // not ideal because coupled to the `html` structure but will do the job for now
        var errorMsgNode = ((NodeChildren) htmlPath.get("html.body.div.div")).get(0);
        assertThat(errorMsgNode.getAttribute("id")).isEqualTo("main-error-message");
    }

    @Test
    void
            startBooking_should_display_index_without_error_msg_if_Booker_found_but_no_Event_is_active() {
        // create an already validated Booker
        var email = "start-booking@test.org";
        createBooker(email, true);
        // get the active Event (creates it if needed)
        var event = getOrCreateActiveEvent();
        // a little dirty, but easy way to stay idempotent while testing without an active Event
        try {
            event.setActive(false);
            eventRepository.save(event);
            var htmlPath =
                    given().pathParam("signature", SIGNATURE)
                            .when()
                            .get("/bookings/{signature}")
                            .then()
                            .statusCode(HttpStatus.OK.value())
                            .contentType(ContentType.HTML)
                            .extract()
                            .htmlPath();

            // check the title to ensure we are in the html generated by the `index.jte`
            // template
            assertThat((String) htmlPath.get("html.head.title")).isEqualTo("Event List");
            // check that the error msg is not present
            // not ideal because coupled to the `html` structure but will do the job for now
            var errorMsgNode = ((NodeChildren) htmlPath.get("html.body.div.div")).get(0);
            assertThat(errorMsgNode.getAttribute("id")).isNotEqualTo("main-error-message");
        } finally {
            // restore the active state of the Event
            event.setActive(true);
            eventRepository.save(event);
        }
    }

    private Event getOrCreateActiveEvent() {
        var activeEvent =
                eventRepository
                        .findByActiveIsTrue()
                        .orElseGet(
                                () -> {
                                    var event = new Event();
                                    event.setTitle("Test Event");
                                    event.setDescription("Test Description");
                                    event.setActive(true);
                                    event.setDate(LocalDate.now());
                                    event = eventRepository.save(event);
                                    CREATED_EVENT_IDS.add(event.getId());
                                    return event;
                                });

        if (activeEvent.getActivities().isEmpty()) {
            // add an Activity to the active Event
            var activity = new Activity();
            activity.setTitle("Test Activity");
            activity.setEvent(activeEvent);
            activity.setStartTime(LocalDateTime.now().plusMinutes(1L));
            activity.setMaxParticipants(12);
            activity.setMaxWaitingQueue(123);
            activeEvent.getActivities().add(activity);
            activeEvent = eventRepository.save(activeEvent);
        }
        return activeEvent;
    }

    static final String SIGNATURE = "signature";

    Booker createBooker(String email, boolean verified) {
        var booker = new Booker();
        booker.setFirstName("For test");
        booker.setLastName("Booker");
        booker.setEmail(email);
        booker.setEmailSignature(SIGNATURE);
        if (verified) {
            booker.setValidationTime(Instant.now());
        }
        CREATED_BOOKER_IDS.add(email);
        return bookerRepository.save(booker);
    }
}
