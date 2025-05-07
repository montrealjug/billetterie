// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import java.time.LocalDate;
import java.util.stream.Collectors;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.montrealjug.billetterie.repository.ActivityRepository;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.montrealjug.billetterie.repository.EventRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is only for local dev convenience to send email to the local `MailHog` container
 * and validate/work on email templates
 * Ideally:
 * - each EmailType should have its dedicated method
 * - each method should be idempotent
 */
@RestController
@Profile("mailhog")
@RequestMapping("/test/email")
public class TestMailController {

    private final EventRepository eventRepository;
    private final ActivityRepository activityRepository;
    private final BookerRepository bookerRepository;
    private final EmailService emailService;

    public TestMailController(
        EventRepository eventRepository,
        ActivityRepository activityRepository,
        BookerRepository bookerRepository,
        EmailService emailService
    ) {
        this.eventRepository = eventRepository;
        this.activityRepository = activityRepository;
        this.bookerRepository = bookerRepository;
        this.emailService = emailService;
    }

    @PostMapping("/after-booking")
    public ResponseEntity<Void> sendAfterBookingEmail() {
        // saving everything in DB is not needed, but it does not hurt 🤷😉 and is a cheap way to
        // test stuffs...
        // this is not the most efficient code, but it's for local dev testing after all...
        var event = eventRepository
            .findByActiveIsTrue()
            .orElseGet(() -> {
                var newEvent = new Event();
                newEvent.setTitle("Test Event for mail notification");
                newEvent.setDescription("Test Event for mail notification description");
                newEvent.setActive(true);
                newEvent.setDate(LocalDate.now().plusDays(7L));
                return eventRepository.save(newEvent);
            });

        var testActivityTitle = "Test Activity for mail notification";
        var activity = event
            .getActivities()
            .stream()
            .filter(a -> a.getTitle().equals(testActivityTitle))
            .findFirst()
            .orElseGet(() -> {
                var newActivity = new Activity();
                newActivity.setTitle(testActivityTitle);
                newActivity.setDescription("Test Activity for mail notification description");
                newActivity.setMaxParticipants(12);
                newActivity.setMaxWaitingQueue(123);
                newActivity.setStartTime(event.getDate().atTime(9, 30));
                newActivity.setEvent(event);
                newActivity = activityRepository.save(newActivity);
                event.getActivities().add(newActivity);
                return newActivity;
            });

        var testEmail = "booker@test.org";
        var booker = bookerRepository
            .findById(testEmail)
            .orElseGet(() -> {
                var newBooker = new Booker();
                newBooker.setFirstName("Booker First Name");
                newBooker.setLastName("Booker Last Name");
                newBooker.setEmail("booker@test.org");
                newBooker = bookerRepository.save(newBooker);
                var firstPart = new Participant();
                firstPart.setFirstName("FirstParticipant First Name");
                firstPart.setLastName("FirstParticipant Last Name");
                firstPart.setBooker(newBooker);
                newBooker.getParticipants().add(firstPart);
                newBooker = bookerRepository.save(newBooker);
                var secondPart = new Participant();
                secondPart.setFirstName("SecondParticipant First Name");
                secondPart.setLastName("SecondParticipant Last Name");
                secondPart.setBooker(newBooker);
                newBooker.getParticipants().add(secondPart);
                bookerRepository.save(newBooker);
                // to get the ids in the created Participants
                return bookerRepository
                    .findById(testEmail)
                    .orElseThrow(() -> new IllegalStateException("we should have found the just" + " created booker!"));
            });

        var bookerParticipantIds = booker
            .getParticipants()
            .stream()
            .map(Participant::getId)
            .collect(Collectors.toSet());
        var participants = activity
            .getParticipants()
            .stream()
            .filter(ap -> bookerParticipantIds.contains(ap.getParticipant().getId()))
            .collect(Collectors.toSet());

        if (participants.isEmpty()) {
            booker
                .getParticipants()
                .forEach(participant -> {
                    var registration = new ActivityParticipant();
                    registration.setParticipant(participant);
                    registration.setActivity(activity);
                    activityRepository.save(activity);
                });
            // to get all ids set properly
            var updatedActivity = activityRepository
                .findById(activity.getId())
                .orElseThrow(() -> new IllegalStateException("we should have found the updated activity!"));
            updatedActivity
                .getParticipants()
                .stream()
                .filter(ap -> bookerParticipantIds.contains(ap.getParticipant().getId()))
                .forEach(participants::add);
        }

        var email = EmailModel.Email.afterBooking(booker, event, participants);
        this.emailService.sendEmail(email);
        return ResponseEntity.accepted().build();
    }
}
