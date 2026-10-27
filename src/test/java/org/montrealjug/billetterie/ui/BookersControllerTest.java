// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailModel.EmailType;
import org.montrealjug.billetterie.email.EmailService;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.montrealjug.billetterie.repository.ActivityRepository;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.montrealjug.billetterie.repository.EventRepository;
import org.montrealjug.billetterie.repository.ParticipantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookersControllerTest {

    // see `src/test/resources/application.yml`
    static final String SESSION_COOKIE = "BILLETTERIE_SESSION";

    @LocalServerPort
    int port;

    @Autowired
    BookerRepository bookerRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @MockitoSpyBean
    EmailService emailService;

    final List<Long> createdEventIds = new ArrayList<>();
    final List<Long> createdActivityIds = new ArrayList<>();
    final List<String> createdBookerEmails = new ArrayList<>();

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @AfterEach
    void tearDown() {
        // activities and events first: their participants reference the bookers' participants
        activityRepository.deleteAllById(createdActivityIds);
        eventRepository.deleteAllById(createdEventIds);
        bookerRepository.deleteAllById(createdBookerEmails);
    }

    @Test
    void sendNoParticipantReminderEmail_should_only_email_recent_bookers_without_participants_in_the_active_event() {
        var activeEvent = getOrCreateActiveEvent();
        var activity = createActivity(activeEvent);
        var activeEventDate = activeEvent.getDate() != null ? activeEvent.getDate() : LocalDate.now();
        var previousEvent = createPastEvent(activeEventDate.minusDays(30));

        // registered before the previous event: not "recent"
        var beforePreviousEvent = previousEvent
            .getDate()
            .minusDays(10)
            .atStartOfDay(ZoneId.of("America/Toronto"))
            .toInstant();
        createBooker("old-no-participant@test.org", beforePreviousEvent);
        // recent, without participant: the only one to email
        createBooker("recent-no-participant@test.org", Instant.now());
        // recent, with a participant in the active event
        var registered = createBooker("recent-with-participant@test.org", Instant.now());
        register(registered, activity, null);

        var count = given()
            .cookie(SESSION_COOKIE, adminSession())
            .contentType(ContentType.JSON)
            .when()
            .post("/admin/bookers/sendNoParticipantReminderEmail")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .extract()
            .jsonPath()
            .getInt("count");

        var captor = ArgumentCaptor.forClass(Email.class);
        verify(emailService, atLeastOnce()).sendEmail(captor.capture());
        var emailedTestBookers = captor
            .getAllValues()
            .stream()
            .filter(email -> email.type() == EmailType.NO_PARTICIPANT_REMINDER)
            .map(email -> email.to().getAddress())
            .filter(createdBookerEmails::contains)
            .toList();
        assertThat(emailedTestBookers).containsExactly("recent-no-participant@test.org");
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void sendNoParticipantReminderEmail_should_require_admin_authentication() {
        given()
            .redirects()
            .follow(false)
            .when()
            .post("/admin/bookers/sendNoParticipantReminderEmail")
            .then()
            .statusCode(302)
            .header("Location", "http://localhost:" + port + "/admin/login");
    }

    @Test
    void bookers_should_only_list_bookers_of_the_active_event_when_filtered() {
        var activeEvent = getOrCreateActiveEvent();
        var activeActivity = createActivity(activeEvent);
        var pastActivity = createActivity(createPastEvent(LocalDate.now().minusDays(30)));
        register(createBooker("active-event-booker@test.org", Instant.now()), activeActivity, null);
        register(createBooker("past-event-booker@test.org", Instant.now()), pastActivity, null);
        createBooker("no-participant-booker@test.org", Instant.now());

        var page = bookersPage("activeEventOnly=true");

        assertThat(page)
            .contains("active-event-booker@test.org")
            .doesNotContain("past-event-booker@test.org")
            .doesNotContain("no-participant-booker@test.org");
        assertThat(bookersPage("activeEventOnly=false")).contains(
            "active-event-booker@test.org",
            "past-event-booker@test.org",
            "no-participant-booker@test.org"
        );
    }

    @Test
    void bookers_should_only_list_bookers_whose_children_never_came_when_filtered() {
        var pastActivity = createActivity(createPastEvent(LocalDate.now().minusDays(30)));
        register(createBooker("no-show-booker@test.org", Instant.now()), pastActivity, null);
        register(createBooker("came-booker@test.org", Instant.now()), pastActivity, Instant.now());
        createBooker("never-registered-booker@test.org", Instant.now());

        var page = bookersPage("noShowOnly=true");

        assertThat(page)
            .contains("no-show-booker@test.org")
            .doesNotContain("came-booker@test.org")
            .doesNotContain("never-registered-booker@test.org");
    }

    @Test
    void checkInPage_should_offer_to_remove_regular_and_waiting_participants() {
        var activity = createActivity(getOrCreateActiveEvent(), 1);
        var booker = createBooker("check-in-booker@test.org", Instant.now());
        var seated = register(booker, activity, null);
        var waiting = register(booker, activity, null);

        var page = given()
            .cookie(SESSION_COOKIE, adminSession())
            .when()
            .get("/admin/bookings/" + booker.getEmailSignature())
            .then()
            .statusCode(200)
            .extract()
            .asString();

        assertThat(page).contains(
            "data-participant-id=\"" + seated.getId() + "\"",
            "data-participant-id=\"" + waiting.getId() + "\""
        );
        assertThat(page.split("remove-participant-button")).hasSize(1 + 2 + 1); // 2 buttons + the script selector
    }

    @Test
    void deleteParticipant_should_not_send_upgrade_email_when_removing_a_waiting_participant() {
        var activity = createActivity(getOrCreateActiveEvent(), 1);
        register(createBooker("seated-booker@test.org", Instant.now()), activity, null);
        var waiting = register(createBooker("waiting-booker@test.org", Instant.now()), activity, null);

        deleteParticipant(activity, waiting);

        verify(emailService, never()).sendEmail(any());
        assertThat(participantRepository.findById(waiting.getId())).isPresent();
        assertThat(bookersPage("activeEventOnly=true")).doesNotContain("waiting-booker@test.org");
    }

    @Test
    void deleteParticipant_should_send_upgrade_email_to_the_first_waiting_participant_when_freeing_a_seat() {
        var activity = createActivity(getOrCreateActiveEvent(), 1);
        var seated = register(createBooker("seated-booker@test.org", Instant.now()), activity, null);
        register(createBooker("waiting-booker@test.org", Instant.now()), activity, null);

        deleteParticipant(activity, seated);

        var captor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(captor.capture());
        assertThat(captor.getValue().type()).isEqualTo(EmailType.PARTICIPANT_UPGRADED);
        assertThat(captor.getValue().to().getAddress()).isEqualTo("waiting-booker@test.org");
    }

    @Test
    void deleteParticipant_should_return_404_for_a_participant_not_in_the_activity() {
        var activity = createActivity(getOrCreateActiveEvent(), 1);
        var participant = register(createBooker("seated-booker@test.org", Instant.now()), activity, null);
        var otherActivity = createActivity(getOrCreateActiveEvent(), 1);

        given()
            .cookie(SESSION_COOKIE, adminSession())
            .when()
            .delete("/admin/activities/" + otherActivity.getId() + "/participants/" + participant.getId())
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value());
        verify(emailService, never()).sendEmail(any());
    }

    private void deleteParticipant(Activity activity, Participant participant) {
        given()
            .cookie(SESSION_COOKIE, adminSession())
            .when()
            .delete("/admin/activities/" + activity.getId() + "/participants/" + participant.getId())
            .then()
            .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String bookersPage(String query) {
        return given()
            .cookie(SESSION_COOKIE, adminSession())
            .when()
            .get("/admin/bookers?" + query)
            .then()
            .statusCode(200)
            .extract()
            .asString();
    }

    private String adminSession() {
        return given()
            .contentType(ContentType.URLENC)
            .formParam("username", "admin")
            .formParam("password", "password")
            .post("/admin/login")
            .then()
            .statusCode(302)
            .extract()
            .cookie(SESSION_COOKIE);
    }

    private Event getOrCreateActiveEvent() {
        return eventRepository.findByActiveIsTrue().orElseGet(() -> {
            var event = new Event();
            event.setTitle("Active test event");
            event.setDescription("Test Description");
            event.setActive(true);
            event.setDate(LocalDate.now().plusDays(10));
            event = eventRepository.save(event);
            createdEventIds.add(event.getId());
            return event;
        });
    }

    private Event createPastEvent(LocalDate date) {
        var event = new Event();
        event.setTitle("Previous test event");
        event.setDescription("Test Description");
        event.setDate(date);
        event = eventRepository.save(event);
        createdEventIds.add(event.getId());
        return event;
    }

    private Activity createActivity(Event event) {
        return createActivity(event, 12);
    }

    private Activity createActivity(Event event, int maxParticipants) {
        var activity = new Activity();
        activity.setTitle("Test Activity");
        activity.setDescription("Test Description");
        activity.setEvent(event);
        activity.setStartTime(LocalDateTime.now().plusDays(1));
        activity.setMaxParticipants(maxParticipants);
        activity.setMaxWaitingQueue(12);
        activity = activityRepository.save(activity);
        createdActivityIds.add(activity.getId());
        return activity;
    }

    private Booker createBooker(String email, Instant creationTime) {
        var booker = new Booker();
        booker.setFirstName("For test");
        booker.setLastName("Booker");
        booker.setEmail(email);
        booker.setEmailSignature("signature-" + email);
        booker.setCreationTime(creationTime);
        createdBookerEmails.add(email);
        return bookerRepository.save(booker);
    }

    private Participant register(Booker booker, Activity activity, Instant checkInTime) {
        var participant = new Participant();
        participant.setFirstName("Registered");
        participant.setLastName("Child");
        participant.setYearOfBirth(2018);
        participant.setBooker(booker);
        participant = participantRepository.save(participant);
        var activityParticipant = new ActivityParticipant();
        activityParticipant.setActivity(activity);
        activityParticipant.setParticipant(participant);
        activityParticipant.getActivityParticipantKey().setActivityId(activity.getId());
        activityParticipant.getActivityParticipantKey().setParticipantId(participant.getId());
        activityParticipant.setCheckInTime(checkInTime);
        activity.getParticipants().add(activityParticipant);
        activityRepository.save(activity);
        return participant;
    }
}
