// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static org.montrealjug.billetterie.ui.Utils.markdownToHtml;
import static org.montrealjug.billetterie.ui.Utils.toPresentationActivities;

import java.util.Optional;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final EventRepository eventRepository;

    public IndexController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Optional<Event> optionalEvent = this.eventRepository.findByActiveIsTrue();

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();
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
            model.addAttribute("event", presentationEvent);
        }

        return "index";
    }
}
