package org.montrealjug.billetterie.ui;

import jakarta.validation.Valid;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/bookers")
public class BookersController {

    private final BookerRepository bookerRepository;

    public BookersController(BookerRepository bookerRepository) {
        this.bookerRepository = bookerRepository;
    }

    @GetMapping("")
    public String bookers(Model model) {
        List<PresentationBooker> presentationBookers  = new ArrayList<>();
        Iterable<Booker> bookers = bookerRepository.findAll();

        bookers.forEach((booker -> {
            presentationBookers.add(new PresentationBooker(booker.getFirstName(),booker.getLastName(),booker.getEmail()));
        }));

        model.addAttribute("bookerList", presentationBookers);

        return "bookers-list";
    }

    @PostMapping("")
    public ResponseEntity<Void> addBooker(@Valid PresentationBooker booker, Model model) {
        Booker bookerEntity = new Booker();
        bookerEntity.setEmail(booker.email());
        bookerEntity.setFirstName(booker.firstName());
        bookerEntity.setLastName(booker.lastName());

        bookerRepository.save(bookerEntity);

        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/admin/bookers")).build();
    }

    @GetMapping("add-booker")
    public String showAddBookerPage(){
        return "bookers-create-update";
    }

    @GetMapping("{email}")
    public String showUpdateBookerPage(Model model, @PathVariable String email) {
        Optional<Booker> optionalBooker = this.bookerRepository.findById(email);

        if (optionalBooker.isPresent()) {
            Booker booker = optionalBooker.get();
            PresentationBooker presentationBooker = new PresentationBooker(booker.getFirstName(), booker.getLastName(), booker.getEmail());
            model.addAttribute("booker", presentationBooker);
        } else {
            throw new RuntimeException("Booker email not present: " + email);
        }

        return "bookers-create-update";
    }


}
