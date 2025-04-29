// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static java.util.stream.Collectors.toList;
import static org.montrealjug.billetterie.ui.Utils.toIndexActivities;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailService;
import org.montrealjug.billetterie.entity.*;
import org.montrealjug.billetterie.exception.EntityNotFoundException;
import org.montrealjug.billetterie.repository.*;
import org.montrealjug.billetterie.service.SignatureService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RegistrationController {
    private static final Logger LOGGER = Logger.getLogger(RegistrationController.class.getName());

    private final BookerRepository bookerRepository;
    private final SignatureService signatureService;
    private final EmailService emailService;
    private final EventRepository eventRepository;
    private final ActivityRepository activityRepository;
    private final ParticipantRepository participantRepository;

    public RegistrationController(
            BookerRepository bookerRepository,
            SignatureService signatureService,
            EmailService emailService,
            EventRepository eventRepository,
            ActivityRepository activityRepository,
            ParticipantRepository participantRepository) {
        this.bookerRepository = bookerRepository;
        this.signatureService = signatureService;
        this.emailService = emailService;
        this.eventRepository = eventRepository;
        this.activityRepository = activityRepository;
        this.participantRepository = participantRepository;
    }

    record BookerCheck(@NotBlank String email) {}

    @PostMapping("/check-returning-booker")
    public ResponseEntity<?> checkReturningBooker(
            @RequestBody @Valid BookerCheck bookerCheck, HttpServletRequest request) {
        return bookerRepository
                .findById(bookerCheck.email().toLowerCase().trim())
                .map(
                        booker -> {
                            var baseUrl = retrieveBaseUrl(request);
                            // someone is trying to use a known but not confirmed email
                            // send the email for confirmation again
                            if (booker.getValidationTime() == null) {
                                emailService.sendEmail(Email.afterRegistration(booker, baseUrl));
                            } else {
                                // send the email for returning Booker otherwise
                                emailService.sendEmail(Email.returningBooker(booker, baseUrl));
                            }
                            return ResponseEntity.noContent().build();
                        })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register-booker")
    public ResponseEntity<?> registerBooker(
            @RequestBody @Valid PresentationBooker booker, HttpServletRequest request)
            throws Exception {
        String email = booker.email().toLowerCase();

        // Check if email already exists in the database
        if (bookerRepository.existsById(email)) {
            // should never happen as it should be tested first via `/check-returning-booker`
            // but let's play it safe if someone tries to call this directly without using our UI
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"message\":\"Email address already registered\"}");
        }

        // Generate a signature for the booker using the email
        var signature = signatureService.signAndTrim(email);

        var bookerEntity = new Booker();
        bookerEntity.setFirstName(booker.firstName());
        bookerEntity.setLastName(booker.lastName());
        bookerEntity.setEmail(email);
        bookerEntity.setEmailSignature(signature);
        bookerRepository.save(bookerEntity);

        var baseUrl = retrieveBaseUrl(request);
        emailService.sendEmail(Email.afterRegistration(bookerEntity, baseUrl));

        return ResponseEntity.status(HttpStatus.CREATED).body(signature);
    }

    record ParticipantSubmission(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Min(2005) int yearOfBirth,
            @NotNull Long activityId,
            @NotBlank String bookerEmailSignature) {}

    @PostMapping("/events/{eventId}/registerParticipant")
    public ResponseEntity<?> registerParticipant(
            @PathVariable Long eventId, @RequestBody @Valid ParticipantSubmission participantSub) {
        // Log the participant information
        LOGGER.info(
                "Received participant registration for event " + eventId + ": " + participantSub);

        try {
            // Check if the activityId matches an existing activity
            Activity activity =
                    activityRepository
                            .findById(participantSub.activityId())
                            .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

            // Check if the emailSignature matches an existing booker
            Booker booker =
                    bookerRepository
                            .findByEmailSignature(participantSub.bookerEmailSignature())
                            .orElseThrow(() -> new EntityNotFoundException("Booker not found"));

            Participant participant =
                    booker.getParticipants().stream()
                            .filter(p -> isSameParticipant(p, participantSub))
                            .findFirst()
                            .orElseGet(
                                    () -> {
                                        // Create and save the participant entity
                                        Participant participantToCreate = new Participant();
                                        participantToCreate.setFirstName(
                                                participantSub.firstName());
                                        participantToCreate.setLastName(participantSub.lastName());
                                        participantToCreate.setYearOfBirth(
                                                participantSub.yearOfBirth());
                                        participantToCreate.setBooker(booker);
                                        return participantRepository.save(participantToCreate);
                                    });

            // Create and save the ActivityParticipant entity
            ActivityParticipant activityParticipant = new ActivityParticipant();
            activityParticipant.setActivity(activity);
            activityParticipant.setParticipant(participant);
            activityParticipant.getActivityParticipantKey().setActivityId(activity.getId());
            activityParticipant.getActivityParticipantKey().setParticipantId(participant.getId());
            activity.getParticipants().add(activityParticipant);

            activityRepository.save(activity);

            // Return a success response with the participant data for display
            return ResponseEntity.ok().body(participantSub);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warning("Data integrity violation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(
                            "{\"message\":\"Failed to register participant due to data integrity"
                                    + " violation\"}");
        } catch (Exception e) {
            LOGGER.severe("Error registering participant: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"message\":\"An error occurred while registering the participant\"}");
        }
    }

    @GetMapping("/bookings/{signature}")
    public String startBooking(@PathVariable String signature, Model model) {
        // Find booker by email signature
        var booker = bookerRepository.findByEmailSignature(signature).orElse(null);

        // Get active event
        var event = eventRepository.findByActiveIsTrue().orElse(null);

        // we found the Booker, and there is an active Event, so Booker can register Participant(s)
        if (booker != null && event != null) {
            // update booker validationTime if not set yet
            if (booker.getValidationTime() == null) {
                booker.setValidationTime(Instant.now());
                bookerRepository.save(booker);
            }

            // Create presentation event
            PresentationEvent presentationEvent =
                    new PresentationEvent(
                            event.getId(),
                            event.getTitle(),
                            event.getDescription(),
                            event.getDate(),
                            toIndexActivities(event.getActivities()),
                            event.isActive(),
                            event.getImagePath());

            // Create a map of activity ID to participants for the current booker
            Map<Long, List<Participant>> activityParticipantsForBooker = new HashMap<>();

            // Create a map of activity ID to total number of participants
            Map<Long, Integer> activityTotalParticipants = new HashMap<>();

            // For each activity in the event
            event.getActivities()
                    .forEach(
                            activity -> {
                                // Get the participants for this activity and this booker
                                List<Participant> participants =
                                        activity.getParticipants().stream()
                                                .map(ActivityParticipant::getParticipant)
                                                .filter(
                                                        participant ->
                                                                participant
                                                                        .getBooker()
                                                                        .equals(booker))
                                                .collect(toList());

                                // Add the participants to the map
                                activityParticipantsForBooker.put(activity.getId(), participants);
                                // Add the total number of participants to the map
                                activityTotalParticipants.put(
                                        activity.getId(), activity.getParticipants().size());
                            });

            // Add attributes to model
            model.addAttribute("event", presentationEvent);
            model.addAttribute("booker", booker);
            model.addAttribute("activityParticipants", activityParticipantsForBooker);
            model.addAttribute("activityTotalParticipants", activityTotalParticipants);

            // Return booker-activities template
            return "booker-activities";
        } else {
            // or the Booker is missing, or there is no active Event, so we go to `index`
            if (event != null) {
                // add the Event for index, if any
                PresentationEvent presentationEvent =
                        new PresentationEvent(
                                event.getId(),
                                event.getTitle(),
                                event.getDescription(),
                                event.getDate(),
                                toIndexActivities(event.getActivities()),
                                event.isActive(),
                                event.getImagePath());
                model.addAttribute("event", presentationEvent);
            }
            if (booker == null) {
                // set the error msg if no Booker found
                model.addAttribute("error", "The booker could not be retrieved from the DB");
            }
            return "index";
        }
    }

    static String retrieveBaseUrl(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String requestURI = request.getRequestURI();

        return requestURL.substring(0, requestURL.length() - requestURI.length())
                + request.getContextPath();
    }

    static boolean isSameParticipant(Participant p, ParticipantSubmission participantSub) {
        return p.getLastName().equalsIgnoreCase(participantSub.lastName())
                && p.getFirstName().equalsIgnoreCase(participantSub.firstName())
                && p.getYearOfBirth() == participantSub.yearOfBirth();
    }
}
