// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ActivityParticipantRegistrationTest {

    @Autowired EventRepository eventRepository;
    @Autowired ActivityRepository activityRepository;
    @Autowired BookerRepository bookerRepository;
    @Autowired ParticipantRepository participantRepository;

    @Test
    void create_event_activity_booker_participants_and_then_assert_properly_saved() {
        // just to get an Active Event, creating one if needed
        var event =
                eventRepository
                        .findByActiveIsTrue()
                        .orElseGet(
                                () -> {
                                    var newEvent = new Event();
                                    newEvent.setTitle("Event Title");
                                    newEvent.setDescription("Event Description");
                                    newEvent.setDate(LocalDate.now());
                                    newEvent.setActive(true);
                                    return eventRepository.save(newEvent);
                                });
        // creating an activity
        var activity = new Activity();
        activity.setTitle("Activity Title");
        activity.setDescription("Activity Description");
        activity.setMaxParticipants(12);
        activity.setMaxWaitingQueue(123);
        activity.setStartTime(LocalDateTime.now());
        activity.setEvent(event);
        // needed to have the id set (and effectively final var)
        var savedActivity = activityRepository.save(activity);

        // we should actually just read the Activity and the Event from DB in the real case
        // start of the part mimicking registration of Participants for a Booker

        // creating a Booker if none exists for the email (the info should be from the front)
        var email = "registration_test@test.org";
        var booker =
                bookerRepository
                        .findById(email)
                        .orElseGet(
                                () -> {
                                    var newBooker = new Booker();
                                    newBooker.setEmail(email);
                                    newBooker.setFirstName("First Name");
                                    newBooker.setLastName("Last Name");
                                    newBooker.setEmailSignature("signature");
                                    return bookerRepository.save(newBooker);
                                });

        // creating Participant in a loop, to be close to what we do in the real flow
        // by looping on the received DTOs from the front
        Stream.of("FirstChild", "SecondChild", "ThirdChild")
                .forEach(
                        name -> {
                            // creating the participant if needed (we should check with both
                            // firstName and lastName)
                            var participant =
                                    booker.getParticipants().stream()
                                            .filter(p -> p.getFirstName().equals(name))
                                            .findFirst()
                                            .orElseGet(
                                                    () -> {
                                                        var newParticipant = new Participant();
                                                        newParticipant.setBooker(booker);
                                                        newParticipant.setFirstName(name);
                                                        newParticipant.setLastName(
                                                                booker.getLastName());
                                                        // needed to have the id set
                                                        return participantRepository.save(
                                                                newParticipant);
                                                    });
                            var registration = new ActivityParticipant();
                            // we need to set the Objects, otherwise JPA complains...
                            // got lazy to find exactly why or an alternative 🤷😅
                            registration.setParticipant(participant);
                            registration.setActivity(savedActivity);
                            // we need to set the ids, otherwise every registration will be equals
                            // and after adding them, we would end up with just one instance in the
                            // Set
                            registration
                                    .getActivityParticipantKey()
                                    .setActivityId(savedActivity.getId());
                            registration
                                    .getActivityParticipantKey()
                                    .setParticipantId(participant.getId());
                            savedActivity.getParticipants().add(registration);
                        });
        // save the ActivityParticipant via the association
        activityRepository.save(savedActivity);

        // let's read the DB and ensure that we have everybody
        var activityFromDb =
                activityRepository
                        .findById(savedActivity.getId())
                        .orElseThrow(() -> new IllegalStateException("Activity not found"));

        assertThat(activityFromDb.getParticipants())
                .hasSize(3)
                .anySatisfy(
                        ap ->
                                assertThat(ap.getParticipant().getFirstName())
                                        .isEqualTo("FirstChild"))
                .anySatisfy(
                        ap ->
                                assertThat(ap.getParticipant().getFirstName())
                                        .isEqualTo("SecondChild"))
                .anySatisfy(
                        ap ->
                                assertThat(ap.getParticipant().getFirstName())
                                        .isEqualTo("ThirdChild"));
    }
}
