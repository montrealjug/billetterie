package org.montrealjug.billetterie.repository;

import org.montrealjug.billetterie.entity.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EventRepository extends CrudRepository<Event, Long> {

    Optional<Event> findByActiveIsTrue();
}