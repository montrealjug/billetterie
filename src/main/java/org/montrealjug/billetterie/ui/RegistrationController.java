package org.montrealjug.billetterie.ui;

import jakarta.validation.Valid;
import org.montrealjug.billetterie.entity.Booker;
import org.montrealjug.billetterie.repository.BookerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RegistrationController {

	private final BookerRepository bookerRepository;

	public RegistrationController(BookerRepository bookerRepository) {
		this.bookerRepository = bookerRepository;
	}

	@PostMapping("/registerBooker")
	public ResponseEntity<Void> registerBooker(@RequestBody @Valid PresentationBooker booker) {
		Booker entity = new Booker();
		entity.setFirstName(booker.firstName());
		entity.setLastName(booker.lastName());
		entity.setEmail(booker.email());
		bookerRepository.save(entity);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
