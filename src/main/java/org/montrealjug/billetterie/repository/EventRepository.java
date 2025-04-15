package org.montrealjug.billetterie.repository;

import org.montrealjug.billetterie.entity.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event, Long> {
}