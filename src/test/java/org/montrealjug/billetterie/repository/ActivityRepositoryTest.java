package org.montrealjug.billetterie.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.montrealjug.billetterie.entity.Activity;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class ActivityRepositoryTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    BookerRepository bookerRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Test
    void registration_of_activity_participants_should_be_ordered_by_registration_time() {
        // create first booker with 2 participants
        var firstBooker = new Booker();
        firstBooker.setFirstName("First");
        firstBooker.setLastName("Booker");
        firstBooker.setEmail("firstBooker@test.org");
        var firstPart = new Participant();
        firstPart.setFirstName("First");
        firstPart.setLastName("Participant");
        firstPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(firstPart);
        firstBooker = bookerRepository.save(firstBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        firstBooker.getParticipants().stream()
                .findFirst()
                .map(Participant::getId)
                .ifPresent(firstPart::setId);
        // as we have a Set and @Id will be set after persistence,
        // we can't add multiple new Participant to a Booker in one shot
        var secondPart = new Participant();
        secondPart.setFirstName("Second");
        secondPart.setLastName("Participant");
        secondPart.setBooker(firstBooker);
        firstBooker.getParticipants().add(secondPart);
        firstBooker = bookerRepository.save(firstBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        firstBooker.getParticipants().stream()
                .filter(p -> p.getId() != firstPart.getId())
                .findFirst()
                .map(Participant::getId)
                .ifPresent(secondPart::setId);

        // create second Booker with 1 Participant
        var secondBooker = new Booker();
        secondBooker.setFirstName("Second");
        secondBooker.setLastName("Booker");
        secondBooker.setEmail("secondBooker@test.org");
        var thirdPart = new Participant();
        thirdPart.setFirstName("Third");
        thirdPart.setLastName("Participant");
        thirdPart.setBooker(secondBooker);
        secondBooker.getParticipants().add(thirdPart);
        secondBooker = bookerRepository.save(secondBooker);
        // jpa does not set the @Id in the associated entity, inefficient, but we're in a test 🤷
        secondBooker.getParticipants().stream()
                .findFirst()
                .map(Participant::getId)
                .ifPresent(thirdPart::setId);

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
        var participantIdList = savedActivity.get().getParticipants().stream()
                .sorted() // `natural` sorting, using `ActivityParticipant#compareTo`
                .map(ActivityParticipant::getParticipant)
                .map(Participant::getId)
                .toList();

        assertThat(participantIdList).containsExactly(thirdPart.getId(), firstPart.getId(), secondPart.getId());
    }
}
