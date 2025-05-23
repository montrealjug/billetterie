// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
public class RepositoryITTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookerRepository bookerRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityParticipantRepository activityParticipantRepository;

    @Autowired
    ParticipantRepository participantRepository;

    @Test
    void registration_of_activity_participants_should_be_ordered_by_registration_time() {
        // create first booker with 2 participants
        var firstBooker = new Booker();
        firstBooker.setFirstName("First");
        firstBooker.setLastName("Booker");
        firstBooker.setEmail("firstBooker@test.org");
        firstBooker.setEmailSignature("pof");
        var firstPart = new Participant();
        firstPart.setFirstName("First");
        firstPart.setLastName("Participant");
        firstPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(firstPart);
        firstBooker = bookerRepository.save(firstBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        firstBooker.getParticipants().stream().findFirst().map(Participant::getId).ifPresent(firstPart::setId);
        // as we have a Set and @Id will be set after persistence,
        // we can't add multiple new Participant to a Booker in one shot
        var secondPart = new Participant();
        secondPart.setFirstName("Second");
        secondPart.setLastName("Participant");
        secondPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(secondPart);
        firstBooker = bookerRepository.save(firstBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        firstBooker
            .getParticipants()
            .stream()
            .filter(p -> p.getId() != firstPart.getId())
            .findFirst()
            .map(Participant::getId)
            .ifPresent(secondPart::setId);

        // create second Booker with 1 Participant
        var secondBooker = new Booker();
        secondBooker.setFirstName("Second");
        secondBooker.setLastName("Booker");
        secondBooker.setEmail("secondBooker@test.org");
        secondBooker.setEmailSignature("pif");
        var thirdPart = new Participant();
        thirdPart.setFirstName("Third");
        thirdPart.setLastName("Participant");
        thirdPart.setBooker(secondBooker);
        secondBooker.getParticipants().add(thirdPart);
        secondBooker = bookerRepository.save(secondBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        secondBooker.getParticipants().stream().findFirst().map(Participant::getId).ifPresent(thirdPart::setId);

        // create the Event
        var event = new Event();
        event.setDate(LocalDate.now().plusDays(2L));
        event.setActive(true);
        event.setDescription("Description");
        event.setTitle("Title");
        event = eventRepository.save(event);

        // create the Activity in the Event
        var activity = new Activity();
        activity.setTitle("Title");
        activity.setDescription("Description");
        activity.setMaxParticipants(12);
        activity.setMaxWaitingQueue(120);
        activity.setStartTime(LocalDateTime.now().plusDays(2L));
        activity.setEvent(event);
        activity = activityRepository.save(activity);

        // create `registrations`
        var firstRegistration = new ActivityParticipant();
        firstRegistration.setActivity(activity);
        firstRegistration.setParticipant(thirdPart);
        activity.getParticipants().add(firstRegistration);
        activity = activityRepository.save(activity);

        var secondRegistration = new ActivityParticipant();
        secondRegistration.setActivity(activity);
        secondRegistration.setParticipant(firstPart);
        activity.getParticipants().add(secondRegistration);
        activity = activityRepository.save(activity);

        var thirdRegistration = new ActivityParticipant();
        thirdRegistration.setActivity(activity);
        thirdRegistration.setParticipant(secondPart);
        activity.getParticipants().add(thirdRegistration);
        activity = activityRepository.save(activity);

        var savedActivity = activityRepository.findById(activity.getId());
        assertThat(savedActivity).isPresent();
        var participantIdList = savedActivity
            .get()
            .getParticipants()
            .stream()
            .sorted() // `natural` sorting, using `ActivityParticipant#compareTo`
            .map(ActivityParticipant::getParticipant)
            .map(Participant::getId)
            .toList();

        assertThat(participantIdList).containsExactly(thirdPart.getId(), firstPart.getId(), secondPart.getId());
    }

    @Test
    void should_find_all_activity_participants_by_event_id_and_booker_email() {
        // create first booker with 2 participants
        var firstBooker = new Booker();
        firstBooker.setFirstName("First");
        firstBooker.setLastName("Booker");
        firstBooker.setEmail("firstBooker@test.org");
        firstBooker.setEmailSignature("pof");
        var firstPart = new Participant();
        firstPart.setFirstName("First");
        firstPart.setLastName("Participant");
        firstPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(firstPart);
        firstBooker = bookerRepository.save(firstBooker);
        firstBooker.getParticipants().stream().findFirst().map(Participant::getId).ifPresent(firstPart::setId);

        var secondPart = new Participant();
        secondPart.setFirstName("Second");
        secondPart.setLastName("Participant");
        secondPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(secondPart);
        firstBooker = bookerRepository.save(firstBooker);
        firstBooker
            .getParticipants()
            .stream()
            .filter(p -> p.getId() != firstPart.getId())
            .findFirst()
            .map(Participant::getId)
            .ifPresent(secondPart::setId);

        // create second Booker with 1 Participant
        var secondBooker = new Booker();
        secondBooker.setFirstName("Second");
        secondBooker.setLastName("Booker");
        secondBooker.setEmail("secondBooker@test.org");
        secondBooker.setEmailSignature("pif");
        var thirdPart = new Participant();
        thirdPart.setFirstName("Third");
        thirdPart.setLastName("Participant");
        thirdPart.setBooker(secondBooker);
        secondBooker.getParticipants().add(thirdPart);
        secondBooker = bookerRepository.save(secondBooker);
        secondBooker.getParticipants().stream().findFirst().map(Participant::getId).ifPresent(thirdPart::setId);

        // create the Event
        var event = new Event();
        event.setDate(LocalDate.now().plusDays(2L));
        event.setActive(true);
        event.setDescription("Description");
        event.setTitle("Title");
        event = eventRepository.save(event);

        // create the Activity in the Event
        var activity = new Activity();
        activity.setTitle("Title");
        activity.setDescription("Description");
        activity.setMaxParticipants(12);
        activity.setMaxWaitingQueue(120);
        activity.setStartTime(LocalDateTime.now().plusDays(2L));
        activity.setEvent(event);
        activity = activityRepository.save(activity);

        // create `registrations`
        var firstRegistration = new ActivityParticipant();
        firstRegistration.setActivity(activity);
        firstRegistration.setParticipant(firstPart);
        activity.getParticipants().add(firstRegistration);
        activity = activityRepository.save(activity);

        var secondRegistration = new ActivityParticipant();
        secondRegistration.setActivity(activity);
        secondRegistration.setParticipant(secondPart);
        activity.getParticipants().add(secondRegistration);
        activity = activityRepository.save(activity);

        var thirdRegistration = new ActivityParticipant();
        thirdRegistration.setActivity(activity);
        thirdRegistration.setParticipant(thirdPart);
        activity.getParticipants().add(thirdRegistration);
        activityRepository.save(activity);

        // Test the finder method for first booker
        List<ActivityParticipant> firstBookerParticipants =
            activityParticipantRepository.findAllActivityParticipantByEventIdAndBookerEmail(
                event.getId(),
                firstBooker.getEmail()
            );

        assertThat(firstBookerParticipants).hasSize(2);
        assertThat(firstBookerParticipants.stream().map(ap -> ap.getParticipant().getId()).toList())
            .containsExactlyInAnyOrder(firstPart.getId(), secondPart.getId());

        // Test the finder method for second booker
        List<ActivityParticipant> secondBookerParticipants =
            activityParticipantRepository.findAllActivityParticipantByEventIdAndBookerEmail(
                event.getId(),
                secondBooker.getEmail()
            );

        assertThat(secondBookerParticipants).hasSize(1);
        assertThat(secondBookerParticipants.stream().map(ap -> ap.getParticipant().getId()).toList())
            .containsExactly(thirdPart.getId());
    }

    @Test
    void create_event_activity_booker_participants_and_then_assert_properly_saved() {
        // just to get an Active Event, creating one if needed
        var event = eventRepository
            .findByActiveIsTrue()
            .orElseGet(() -> {
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
        var booker = bookerRepository
            .findById(email)
            .orElseGet(() -> {
                var newBooker = new Booker();
                newBooker.setEmail(email);
                newBooker.setFirstName("First Name");
                newBooker.setLastName("Last Name");
                newBooker.setEmailSignature("signature");
                return bookerRepository.save(newBooker);
            });

        // creating Participant in a loop, to be close to what we do in the real flow
        // by looping on the received DTOs from the front
        Stream
            .of("FirstChild", "SecondChild", "ThirdChild")
            .forEach(name -> {
                // creating the participant if needed (we should check with both
                // firstName and lastName)
                var participant = booker
                    .getParticipants()
                    .stream()
                    .filter(p -> p.getFirstName().equals(name))
                    .findFirst()
                    .orElseGet(() -> {
                        var newParticipant = new Participant();
                        newParticipant.setBooker(booker);
                        newParticipant.setFirstName(name);
                        newParticipant.setLastName(booker.getLastName());
                        // needed to have the id set
                        return participantRepository.save(newParticipant);
                    });
                var registration = new ActivityParticipant();
                // we need to set the Objects, otherwise JPA complains...
                // got lazy to find exactly why or an alternative 🤷😅
                registration.setParticipant(participant);
                registration.setActivity(savedActivity);
                // we need to set the ids, otherwise every registration will be equals
                // and after adding them, we would end up with just one instance in the
                // Set
                registration.getActivityParticipantKey().setActivityId(savedActivity.getId());
                registration.getActivityParticipantKey().setParticipantId(participant.getId());
                savedActivity.getParticipants().add(registration);
            });
        // save the ActivityParticipant via the association
        activityRepository.save(savedActivity);

        // let's read the DB and ensure that we have everybody
        var activityFromDb = activityRepository
            .findById(savedActivity.getId())
            .orElseThrow(() -> new IllegalStateException("Activity not found"));

        assertThat(activityFromDb.getParticipants())
            .hasSize(3)
            .anySatisfy(ap -> assertThat(ap.getParticipant().getFirstName()).isEqualTo("FirstChild"))
            .anySatisfy(ap -> assertThat(ap.getParticipant().getFirstName()).isEqualTo("SecondChild"))
            .anySatisfy(ap -> assertThat(ap.getParticipant().getFirstName()).isEqualTo("ThirdChild"));
    }
}
