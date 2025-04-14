package org.montrealjug.billetterie.jte;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.montrealjug.billetterie.Activity;
import org.montrealjug.billetterie.ActivityRepository;
import org.montrealjug.billetterie.Event;
import org.montrealjug.billetterie.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Controller
public class EventsController {

    private final EventRepository eventRepository;
    private final ActivityRepository activityRepository;

    public EventsController(EventRepository eventRepository, ActivityRepository activityRepository) {
        this.eventRepository = eventRepository;
        this.activityRepository = activityRepository;
    }

    @GetMapping("/events")
    public String events(Model model) {
        List<PresentationEvent> presentationEvents = new ArrayList<>();
        Iterable<Event> events = this.eventRepository.findAll();
        events.forEach(event -> {
            PresentationEvent presentationEvent = new PresentationEvent(event.getId(), event.getTitle(), event.getDescription(), event.getDate(), toIndexActivities(event.getActivities()), event.isActive());
            presentationEvents.add(presentationEvent);
        });
        model.addAttribute("events", presentationEvents);
        return "events-list";
    }


    @GetMapping("/events/{id}")
    public String event(Model model, @PathVariable long id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        PresentationEvent presentationEvent;
        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            presentationEvent = new PresentationEvent(event.getId(), event.getTitle(), event.getDescription(), event.getDate(), Collections.emptyList(), event.isActive());
        } else {
            throw new RuntimeException("Event with id " + id + " not found");
        }
        model.addAttribute("event", presentationEvent);
        return "events-create-update";
    }

    @PostMapping("/events/{id}")
    public ResponseEntity<Void> updateEvent(@Valid PresentationEvent presentationEvent, Model model, @PathVariable long id) {
        Optional<Event> optionalEvent = this.eventRepository.findById(id);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
            event.setTitle(presentationEvent.title);
            event.setDescription(presentationEvent.description);
            event.setDate(presentationEvent.date);
            event.setActive(presentationEvent.active() !=null ? presentationEvent.active : false);
            eventRepository.save(event);
        } else {
            throw new RuntimeException("Event with id " + id + " not found");
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/events"))
                .build();

    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> delete(Model model, @PathVariable long id) {
        this.eventRepository.deleteById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();

    }


    @GetMapping("/events/createEvent")
    public String createEvent(Model model) {
        return "events-create-update";
    }


    @PostMapping("/events")
    public ResponseEntity<Void> createEvents(@Valid PresentationEvent event, Model model) {
        Event entity = new Event();
        entity.setDescription(event.description);
        entity.setTitle(event.title);
        entity.setDate(event.date);
        eventRepository.save(entity);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/events"))
                .build();

    }


    @GetMapping("/events/{id}/createActivity")
    public String createActivity(Model model, @PathVariable final Long id) {
        model.addAttribute("eventId", id);
        return "activities-create-update";
    }


    @GetMapping("/events/{eventId}/activities/{activityId}")
    public String activity(Model model, @PathVariable long eventId, @PathVariable Long activityId) {
        Optional<Activity> optionalActivity = this.activityRepository.findById(activityId);
        PresentationActivity presentationActivity;
        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            presentationActivity = new PresentationActivity(activity.getId(), activity.getTitle(), activity.getDescription(), activity.getStartTime().toLocalTime());
        } else {
            throw new RuntimeException("Activity with id " + activityId + " not found");
        }
        model.addAttribute("activity", presentationActivity);
        model.addAttribute("eventId", eventId);
        return "activities-create-update";
    }

    @PostMapping("/events/{eventId}/activities/{activityId}")
    public ResponseEntity<Void> updateActivity(@Valid PresentationActivity presentationActivity, Model model, @PathVariable long eventId, @PathVariable long activityId) {
        Optional<Activity> optionalActivity = activityRepository.findById(activityId);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            activity.setTitle(presentationActivity.title);
            activity.setDescription(presentationActivity.description);

            eventRepository.findById(eventId).ifPresent(event -> {
                LocalDate date = event.getDate();
                LocalDateTime localDateTime = date.atTime(presentationActivity.time);
                activity.setStartTime(localDateTime);
            });

            activityRepository.save(activity);
        } else {
            throw new RuntimeException("Activity with id " + activityId + " not found");
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/events"))
                .build();

    }

    @PostMapping("/events/{id}/activities")
    public ResponseEntity<Void> saveActivity(@Valid PresentationActivity activity, Model model, @PathVariable final Long id) {

        Optional<Event> byId = eventRepository.findById(id);

        byId.ifPresent(event -> {
            Activity entity = new Activity();
            entity.setDescription(activity.description);
            entity.setTitle(activity.title);

            LocalDate date = event.getDate();
            LocalDateTime localDateTime = date.atTime(activity.time);

            entity.setStartTime(localDateTime);
            event.getActivities().add(entity);
            activityRepository.save(entity);
            eventRepository.save(event);

        });
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/events"))
                .build();

    }

    @DeleteMapping("/events/{eventId}/activities/{activityId}")
    public ResponseEntity<Void> deleteActivity(Model model, @PathVariable long eventId, @PathVariable long activityId) {
        this.eventRepository.findById(eventId).ifPresent(event -> {
            Optional<Activity> optionalActivity = activityRepository.findById(activityId);
            optionalActivity.ifPresent(activity -> {
                event.getActivities().remove(activity);
                eventRepository.save(event);
                activityRepository.delete(activity);
            });
        });
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();

    }


    private List<PresentationActivity> toIndexActivities(Set<org.montrealjug.billetterie.Activity> activities) {
        List<PresentationActivity> indexActivities = new ArrayList<>();
        activities.forEach(activity -> {
            PresentationActivity indexActivity = new PresentationActivity(activity.getId(), activity.getTitle(), activity.getDescription(), activity.getStartTime().toLocalTime());
            indexActivities.add(indexActivity);
        });
        return indexActivities;

    }


    public record PresentationEvent(Long id, String title, String description, LocalDate date,
                                    List<PresentationActivity> activities, Boolean active) {
    }

    public record PresentationActivity(Long id, @NotBlank String title, @NotBlank String description,
                                       LocalTime time) {
    }
}
