// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static org.montrealjug.billetterie.ui.Utils.*;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Optional;
import org.montrealjug.billetterie.entity.*;
import org.montrealjug.billetterie.exception.EntityNotFoundException;
import org.montrealjug.billetterie.repository.ActivityRepository;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.montrealjug.billetterie.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CheckInController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckInController.class.getName());

    private final BookerRepository bookerRepository;
    private final EventRepository eventRepository;
    private final ActivityRepository activityRepository;

    public CheckInController(
        BookerRepository bookerRepository,
        EventRepository eventRepository,
        ActivityRepository activityRepository
    ) {
        this.bookerRepository = bookerRepository;
        this.eventRepository = eventRepository;
        this.activityRepository = activityRepository;
    }

    public record CheckInRequest(@NotNull Long activityId, @NotNull Long participantId, boolean checked) {}

    @GetMapping("/admin/bookings/{signature}")
    public String startCheckIn(@PathVariable String signature, Model model) {
        Optional<Booker> optionalBooker = bookerRepository.findByEmailSignature(signature);
        Optional<Event> optionalEvent = eventRepository.findByActiveIsTrue();

        // we found the Booker, and there is an active Event, so admin can check in participants
        if (optionalBooker.isPresent() && optionalEvent.isPresent()) {
            var booker = optionalBooker.get();
            var event = optionalEvent.get();
            // Create presentation event
            PresentationEvent presentationEvent = new PresentationEvent(
                event.getId(),
                event.getTitle(),
                markdownToHtml(event.getDescription()),
                event.getDate(),
                toPresentationActivities(event.getActivities()),
                event.isActive(),
                event.getImagePath(),
                event.getLocation()
            );

            // Add attributes to model
            model.addAttribute("event", presentationEvent);
            model.addAttribute("booker", booker);

            // Return checkin template
            return "checkin";
        } else {
            throw new org.montrealjug.billetterie.exception.EntityNotFoundException("Booker or Event not found");
        }
    }

    @PutMapping("/admin/bookings/checkin")
    public ResponseEntity<?> checkInParticipant(@RequestBody CheckInRequest checkInRequest) {
        try {
            Activity activity = activityRepository
                .findById(checkInRequest.activityId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

            // Find the participant in the activity
            ActivityParticipant activityParticipant = activity
                .getParticipants()
                .stream()
                .filter(ap -> ap.getParticipant().getId() == checkInRequest.participantId())
                .findFirst()
                .orElseGet(() ->
                    activity
                        .getWaitingParticipants()
                        .stream()
                        .filter(ap -> ap.getParticipant().getId() == checkInRequest.participantId())
                        .findFirst()
                        .orElseThrow(() -> new EntityNotFoundException("Participant not found in this activity"))
                );

            // Update check-in time based on the checked status
            if (checkInRequest.checked()) {
                activityParticipant.setCheckInTime(Instant.now());
            } else {
                activityParticipant.setCheckInTime(null);
            }

            // Save the updated activity
            activityRepository.save(activity);

            // Return success response
            if (checkInRequest.checked()) {
                return ResponseEntity.ok().body("{\"message\":\"Participant successfully checked in\"}");
            } else {
                return ResponseEntity.ok().body("{\"message\":\"Participant successfully UNchecked\"}");
            }
        } catch (Exception e) {
            LOGGER.error("Error checking in participant: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"message\":\"An error occurred while checking in the participant\"}");
        }
    }
}
