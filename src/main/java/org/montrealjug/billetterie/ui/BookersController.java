// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static org.montrealjug.billetterie.ui.RegistrationController.retrieveBaseUrl;
import static org.montrealjug.billetterie.ui.Utils.toPresentationActivityParticipants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.montrealjug.billetterie.email.EmailModel;
import org.montrealjug.billetterie.email.EmailService;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.exception.EntityNotFoundException;
import org.montrealjug.billetterie.exception.RedirectableNotFoundException;
import org.montrealjug.billetterie.repository.ActivityParticipantRepository;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.montrealjug.billetterie.repository.EventRepository;
import org.montrealjug.billetterie.service.QrCodeService;
import org.montrealjug.billetterie.service.SignatureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/bookers")
public class BookersController {

    private final BookerRepository bookerRepository;
    private final SignatureService signatureService;
    private final EventRepository eventRepository;
    private final ActivityParticipantRepository activityParticipantRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    public BookersController(
        BookerRepository bookerRepository,
        SignatureService signatureService,
        EventRepository eventRepository,
        ActivityParticipantRepository activityParticipantRepository,
        EmailService emailService,
        QrCodeService qrCodeService
    ) {
        this.bookerRepository = bookerRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
        this.signatureService = signatureService;
        this.eventRepository = eventRepository;
        this.activityParticipantRepository = activityParticipantRepository;
    }

    @GetMapping("")
    public String bookers(Model model) {
        Iterable<Booker> bookers = bookerRepository.findAll();
        Iterable<Event> allEvents = eventRepository.findAll();
        List<PresentationBookerWithParticipants> presentationBookers;
        presentationBookers =
            StreamSupport
                .stream(bookers.spliterator(), false)
                .map(booker ->
                    new PresentationBookerWithParticipants(
                        booker.getFirstName(),
                        booker.getLastName(),
                        booker.getEmail(),
                        booker.getEmailSignature(),
                        StreamSupport
                            .stream(allEvents.spliterator(), false)
                            .collect(
                                Collectors.toMap(
                                    event -> event,
                                    event ->
                                        activityParticipantRepository.findAllActivityParticipantByEventIdAndBookerEmail(
                                            event.getId(),
                                            booker.getEmail()
                                        )
                                )
                            )
                    )
                )
                .toList();

        model.addAttribute("bookerList", presentationBookers);
        model.addAttribute("allEvents", StreamSupport.stream(allEvents.spliterator(), false).toList());

        return "bookers-list";
    }

    @PostMapping("")
    public ResponseEntity<Void> addBooker(@Valid PresentationBooker booker, Model model) throws Exception {
        Booker bookerEntity = new Booker();

        String email = booker.email().toLowerCase();

        if (bookerRepository.existsById(email)) {
            throw new RuntimeException("User with this email already exists");
        }

        bookerEntity.setEmail(email);
        bookerEntity.setFirstName(booker.firstName());
        bookerEntity.setLastName(booker.lastName());
        bookerEntity.setValidationTime(Instant.now());
        bookerEntity.setEmailSignature(signatureService.signAndTrim(email));

        bookerRepository.save(bookerEntity);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/admin/bookers")).build();
    }

    @GetMapping("add-booker")
    public String showAddBookerPage() {
        return "bookers-create-update";
    }

    @GetMapping("{email}")
    public String showUpdateBookerPage(Model model, @PathVariable String email) {
        Optional<Booker> optionalBooker = this.bookerRepository.findById(email);

        if (optionalBooker.isEmpty()) {
            throw new EntityNotFoundException("Booker with email " + email + " not found", "bookers-create-update");
        }

        Booker booker = optionalBooker.get();
        PresentationBooker presentationBooker = new PresentationBooker(
            booker.getFirstName(),
            booker.getLastName(),
            booker.getEmail()
        );
        model.addAttribute("booker", presentationBooker);

        return "bookers-create-update";
    }

    @PostMapping("/sendReminderEmail")
    public ResponseEntity<Void> sendReminderEmail(HttpServletRequest request) {
        eventRepository
            .findByActiveIsTrue()
            .ifPresent(event ->
                bookerRepository
                    .findAll()
                    .forEach(booker -> {
                        List<ActivityParticipant> bookings =
                            activityParticipantRepository.findAllActivityParticipantByEventIdAndBookerEmail(
                                event.getId(),
                                booker.getEmail()
                            );
                        if (!bookings.isEmpty()) {
                            try {
                                emailService.sendEmail(
                                    EmailModel.Email.lastReminder(
                                        booker,
                                        toPresentationActivityParticipants(bookings),
                                        event,
                                        retrieveBaseUrl(request),
                                        qrCodeService.generateQrCode(
                                            retrieveBaseUrl(request) + "/admin/bookings/" + booker.getEmailSignature()
                                        )
                                    )
                                );
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    })
            );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("{email}")
    public ResponseEntity<Void> updateBooker(@PathVariable String email, @Valid PresentationBooker presentationBooker) {
        Optional<Booker> optionalBooker = bookerRepository.findById(email);

        if (optionalBooker.isEmpty()) {
            throw new RedirectableNotFoundException(
                "Booker with email " + email + " not found",
                "/admin/bookers/" + email
            );
        }

        Booker booker = optionalBooker.get();

        booker.setFirstName(presentationBooker.firstName());
        booker.setLastName(presentationBooker.lastName());

        //         TODO allow changing user's email address (currently unable since email is the
        // primary key
        //         of Booker)

        //        String updatedEmail = presentationBooker.email();
        //        if (!email.equals(updatedEmail)) {
        //            if (bookerRepository.existsById(updatedEmail)) {
        //                throw new RuntimeException("User with this email already exists");
        //            }
        //
        //            booker.setEmail(updatedEmail);
        //            booker.setEmailSignature(signatureService.signAndTrim(updatedEmail));
        //            booker.setValidationTime(Instant.now());
        //        }

        bookerRepository.save(booker);

        // return to the new email as url path variable if changed
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/admin/bookers")).build();
    }
}
